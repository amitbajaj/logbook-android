package `in`.bajajtech.apps.utils

import `in`.bajajtech.apps.logbook.Constants
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object HTTPPostHelper {
    fun doHTTPPost(urlString: String, sessionId: String, data: String): Pair<String,String>? {
        val url = URL(urlString)
        var connection: HttpsURLConnection? = null
        var sessionCookie: String = ""
//        println("I am here")
        return try {
            connection = (url.openConnection() as? HttpsURLConnection)
            connection?.run {
                // Timeout for reading InputStream arbitrarily set to 10000ms.
                readTimeout = 10000
                // Timeout for connection.connect() arbitrarily set to 10000ms.
                connectTimeout = 10000
                // For this use case, set HTTP method to GET.
                requestMethod = "POST"
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                doInput = true
                // Request is carrying an output (POST data).
                doOutput = true
                // Disable caching
                useCaches = false
                //Set the cookie if one is provided
                if(sessionId.isNotEmpty()) {
                    setRequestProperty(
                        "Cookie",
                        Constants.SessionCookieName.plus("=").plus(sessionId).plus(";")
                    )
                }
                // Open communications link (network traffic occurs here).
//                println("Writing post data")
                outputStream?.let { stream ->
                    val oStream = DataOutputStream(stream)
                    oStream.writeBytes(data)
                }
//                println("Post data complete")
//                publishProgress(CONNECT_SUCCESS)
                if (responseCode != HttpsURLConnection.HTTP_OK) {
//                    println("Invalid response code ".plus(responseCode.toString()))
                    throw IOException("HTTP error code: $responseCode")
                }
//                println("Response Received")
                // Retrieve the Session Cookie
//                publishProgress(PROCESS_SESSION_COOKIE_IN_PROGRESS)
                headerFields["Set-Cookie"]?.let { cookies ->
//                        println("Looking for cookies")
//                        println("There are some cookies")
                    val cookieIterator = cookies.iterator()
                    var cookie: String
                    var cookieAttributes: List<String>
                    var keyPair: List<String>
                    while (cookieIterator.hasNext()) {
                        cookie = cookieIterator.next()
//                                println(cookie)
                        cookieAttributes = cookie.split(";")
                        keyPair = cookieAttributes[0].split("=")
                        if (keyPair[0] == Constants.SessionCookieName) {
//                                    println("Found the matching one")
                            sessionCookie = keyPair[1]
                            break
                        }
                    }
                }
//                println("Processed headers")
                // Retrieve the response body as an InputStream.
//                publishProgress(GET_INPUT_STREAM_SUCCESS, 0)
                inputStream?.let { stream ->
                    // Converts Stream to String with max length of 50000.
                    Pair(sessionCookie, readStream(stream))
                }
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
//            println("Closing the stream...")
            connection?.inputStream?.close()
            connection?.disconnect()
        }
    }

    @Throws(IOException::class, UnsupportedEncodingException::class)
    fun readStream(stream: InputStream): String{
        val bReader = BufferedReader(InputStreamReader(stream))
        var line: String?
        var response = ""
        line = bReader.readLine()
        while(line!=null){
            response = response.plus(line)
            line=bReader.readLine()
        }
        bReader.close()
//        publishProgress(PROCESS_INPUT_STREAM_SUCCESS)
//        println("Processing incoming data ".plus(response))
        return response
    }
}