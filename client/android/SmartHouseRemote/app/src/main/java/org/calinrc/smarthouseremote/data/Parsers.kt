package org.calinrc.smarthouseremote.data

import java.io.BufferedReader
import java.io.InputStream


sealed class HomeResponse {
    data class HomeStatusResponse(val statusCode: Int, val response: String) : HomeResponse() {
        override fun toString(): String {
            return "$statusCode - $response"
        }
    }

    data class HomeGateChangeResponse(val statusCode: Int, val response: String) : HomeResponse() {
        override fun toString(): String {
            return "$statusCode - $response"
        }
    }

    data class HomeDoorChangeResponse(val statusCode: Int, val response: String) : HomeResponse() {
        override fun toString(): String {
            return "$statusCode - $response"
        }
    }

    data class FailedHomeResponse(val statusCode: Int, val exception: Exception, val isRetriable:Boolean=false) :
        HomeResponse() {
        override fun toString(): String {
            return "$statusCode - ${exception.message}"

        }
    }
}

class ParsersFactory {
    fun getStatusParser():HomeServerParser<HomeResponse.HomeStatusResponse>{
        return HomeServerParser.HomeServerStatusResponseParser()
    }
    fun getGateChangeParser():HomeServerParser<HomeResponse.HomeGateChangeResponse>{
        return HomeServerParser.HomeServerGateChangeResponseParser()
    }
    fun getDoorChangeParser():HomeServerParser<HomeResponse.HomeDoorChangeResponse>{
        return HomeServerParser.HomeServerDoorChangeResponseParser()
    }

}

sealed class HomeServerParser<out T>{

    abstract fun parse(responseCode: Int, inputStream: InputStream):T

    fun extractStr(inputStream: InputStream):String {
        val reader = BufferedReader(inputStream.reader())
        val content = StringBuilder()
        try {
            var line = reader.readLine()
            while (line != null) {
                content.append(line)
                line = reader.readLine()
            }
        } finally {
            reader.close()
        }
        return content.toString()
    }

    class HomeServerStatusResponseParser: HomeServerParser<HomeResponse.HomeStatusResponse>(){
        override fun parse(responseCode: Int, inputStream: InputStream): HomeResponse.HomeStatusResponse {
            return HomeResponse.HomeStatusResponse(responseCode, extractStr(inputStream))
        }
    }

    class HomeServerGateChangeResponseParser: HomeServerParser<HomeResponse.HomeGateChangeResponse>(){
        override fun parse(responseCode: Int, inputStream: InputStream): HomeResponse.HomeGateChangeResponse {
            return HomeResponse.HomeGateChangeResponse(responseCode, extractStr(inputStream))
        }
    }

    class HomeServerDoorChangeResponseParser: HomeServerParser<HomeResponse.HomeDoorChangeResponse>(){
        override fun parse(responseCode: Int, inputStream: InputStream): HomeResponse.HomeDoorChangeResponse {
            return HomeResponse.HomeDoorChangeResponse(responseCode, extractStr(inputStream))
        }
    }



}

