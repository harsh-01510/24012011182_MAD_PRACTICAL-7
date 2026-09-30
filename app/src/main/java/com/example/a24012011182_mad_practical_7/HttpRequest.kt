package com.example.a24012011182_mad_practical_7


import android.nfc.Tag
import android.util.Log
import  kotlin.math.log
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpRequest {
    val TAG="Http"
    fun makeServiceCall(reqUrl: String, token: String): String? {
        var response: String? = null
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer ${token.trim()}")
            conn.setRequestProperty("Content-Type", "application/json")
            response = convertStreamToString(BufferedInputStream(conn.getInputStream()))
        } catch (e: Exception) {
            Log.e(TAG, "makeServiceCall error: ${e.message}", e)
        }
        return response
    }
    private fun convertStreamToString(iss: java.io.BufferedInputStream):String?{
        val reader = BufferedReader(InputStreamReader(iss))
        val sb = StringBuilder()
        var line:String?
        try{
            while(reader.readLine().also{line=it}!=null){
                sb.append(line).append("\n")
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
        finally {
            try {
                iss.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}