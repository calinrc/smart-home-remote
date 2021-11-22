package org.calinrc.smarthouseremote.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.Authenticator
import java.net.PasswordAuthentication


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
                        setRequestProperty("Content-Type", "application/json")
                        setRequestProperty("Accept", "application/json")
                        doOutput = hasOutput
                        connectTimeout = 1000
                        try {
                            if (hasOutput) {
                                outputStream.write(body.toByteArray())
                                outputStream.close()
                            }
                            val rc = responseCode
                            if (rc / 100 == 2)
                                try {
                                    parser.parse(rc, inputStream)
                                } catch (e: Exception) {
                                    HomeResponse.FailedHomeResponse(
                                        rc,
                                        RuntimeException("Unknown failure: " + e.message)
                                    )
                                }
                            else {
                                HomeResponse.FailedHomeResponse(
                                    rc,
                                    RuntimeException(
                                        try {
                                            HomeServerParser.extractStr(errorStream)
                                        } catch (e: Exception) {
                                            "Invalid response code $rc"
                                        }
                                    )
                                )
                            }
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