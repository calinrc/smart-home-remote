package org.calinrc.smarthouseremote.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        return withContext(Dispatchers.IO) {
            val url = URL(creds.hostUrl + "/home/state")
            Authenticator.setDefault(BasicAuthenticator(creds.username, creds.password))
            (url.openConnection() as? HttpURLConnection)?.run {
                requestMethod = "GET"
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                doOutput = false
                connectTimeout = 1000
                //outputStream.write(jsonBody.toByteArray())
                try {
                    val rc = responseCode
                    if (rc >= 200 && rc < 300)
                        responseParserFactory.getStatusParser().parse(responseCode, inputStream)
                    else
                        HomeResponse.FailedHomeResponse(
                            responseCode,
                            RuntimeException("Invalid response code $responseCode")
                        )
                } catch (e: java.net.ProtocolException) {
                    HomeResponse.FailedHomeResponse(400, e)
                } catch (e: Exception) {
                    HomeResponse.FailedHomeResponse(500, e)
                }
            }
                ?: HomeResponse.FailedHomeResponse(
                    500,
                    RuntimeException("Cannot open HttpURLConnection")
                )
        }

    }

    suspend fun gateStateChange(): HomeResponse {
        return withContext(Dispatchers.IO) {
            val url = URL(creds.hostUrl + "/home/gate")
            Authenticator.setDefault(BasicAuthenticator(creds.username, creds.password))
            (url.openConnection() as? HttpURLConnection)?.run {
                requestMethod = "PUT"
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                connectTimeout = 1000
                outputStream.write("{\"home\":{\"gate\":\"change\"}}".toByteArray())
                try {
                    val rc = responseCode
                    if (rc >= 200 && rc < 300)
                        responseParserFactory.getGateChangeParser().parse(responseCode, inputStream)
                    else
                        HomeResponse.FailedHomeResponse(
                            responseCode,
                            RuntimeException("Invalid response code $responseCode")
                        )
                } catch (e: java.net.ProtocolException) {
                    HomeResponse.FailedHomeResponse(400, e)
                } catch (e: Exception) {
                    HomeResponse.FailedHomeResponse(500, e)
                }
            }
                ?: HomeResponse.FailedHomeResponse(
                    500,
                    RuntimeException("Cannot open HttpURLConnection")
                )
        }
    }

    suspend fun doorStateChange(): HomeResponse {
        return withContext(Dispatchers.IO) {
            val url = URL(creds.hostUrl + "/home/door")
            Authenticator.setDefault(BasicAuthenticator(creds.username, creds.password))
            (url.openConnection() as? HttpURLConnection)?.run {
                requestMethod = "PUT"
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                connectTimeout = 1000
                outputStream.write("{\"home\":{\"door\":\"change\"}}".toByteArray())
                try {
                    val rc = responseCode
                    if (rc >= 200 && rc < 300)
                        responseParserFactory.getDoorChangeParser().parse(responseCode, inputStream)
                    else
                        HomeResponse.FailedHomeResponse(
                            responseCode,
                            RuntimeException("Invalid response code $responseCode")
                        )
                } catch (e: java.net.ProtocolException) {
                    HomeResponse.FailedHomeResponse(400, e)
                } catch (e: Exception) {
                    HomeResponse.FailedHomeResponse(500, e)
                }
            }
                ?: HomeResponse.FailedHomeResponse(
                    500,
                    RuntimeException("Cannot open HttpURLConnection")
                )
        }

    }
}