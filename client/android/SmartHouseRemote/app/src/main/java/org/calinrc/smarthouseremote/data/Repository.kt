package org.calinrc.smarthouseremote.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.Authenticator
import java.net.PasswordAuthentication


//sealed class Result<out R> {
//    data class Success<out T>(val data: T) : Result<T>()
//    data class Error(val exception: Exception) : Result<Nothing>()
//}


class BasicAuthenticator(val username: String, val password: String) : Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(username, password.toCharArray())
    }
}

class ServerCredentials(val hostUrl: String, val username: String, val password: String)

class HomeServerRepository(
    private val creds: ServerCredentials,
    private val responseParserFactory: ParsersFactory
) {

    suspend fun getStatus(): HomeResponse {
        return httpRequest("/home/state", "GET", false, "", responseParserFactory.getStatusParser())
    }


    suspend fun gateStateChange(): HomeResponse {
        return httpRequest(
            "/home/gate",
            "PUT",
            true,
            "{\"home\":{\"gate\":\"change\"}}",
            responseParserFactory.getGateChangeParser()
        )
    }


    suspend fun doorStateChange(): HomeResponse {
        return httpRequest(
            "/home/door",
            "PUT",
            true,
            "{\"home\":{\"door\":\"change\"}}",
            responseParserFactory.getDoorChangeParser()
        )
    }

    private suspend fun <T : HomeResponse> httpRequest(
        uriPath: String,
        method: String,
        hasOutput: Boolean,
        body: String,
        parser: HomeServerParser<T>
    ): HomeResponse {
        return withContext(Dispatchers.IO) {
            if (creds.hostUrl.isNotBlank()) {
                try {
                    val url = URL(creds.hostUrl + uriPath)
                    Authenticator.setDefault(BasicAuthenticator(creds.username, creds.password))
                    (url.openConnection() as? HttpURLConnection)?.run {
                        requestMethod = method
                        setRequestProperty("Content-Type", "application/json; utf-8")
                        setRequestProperty("Accept", "application/json")
                        doOutput = hasOutput
                        connectTimeout = 1000
                        try {
                            if (hasOutput)
                                outputStream.write(body.toByteArray())
                            val rc = responseCode
                            if (rc >= 200 && rc < 300)
                                parser.parse(responseCode, inputStream)
                            else
                                HomeResponse.FailedHomeResponse(
                                    responseCode,
                                    RuntimeException("Invalid response code $responseCode")
                                )
                        } catch (e: java.net.ProtocolException) {
                            HomeResponse.FailedHomeResponse(400, e)

                        } catch (ioEx: IOException) {
                            HomeResponse.FailedHomeResponse(500, ioEx)
                        } catch (e: Exception) {
                            HomeResponse.FailedHomeResponse(500, e)
                        }
                    }
                        ?: HomeResponse.FailedHomeResponse(
                            500, RuntimeException("Cannot open HttpURLConnection")
                        )
                } catch (e: Exception) {
                    HomeResponse.FailedHomeResponse(400, e)
                }
            } else {
                HomeResponse.FailedHomeResponse(400, RuntimeException("Invalid host"))
            }
        }
    }
}