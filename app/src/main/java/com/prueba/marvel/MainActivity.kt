package com.prueba.marvel

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRequest = findViewById<Button>(R.id.btn_request)
        btnRequest.setOnClickListener { characterListRequest() }
    }

    private fun characterListRequest() {
        val textView = findViewById<TextView>(R.id.tv_result)

        val queue = Volley.newRequestQueue(this)
        var baseUrl = "https://gateway.marvel.com:443/v1/public/characters?"

        baseUrl = baseUrl + "ts=1&" +
                "apikey=" + BuildConfig.API_KEY +
                "&hash=" + calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)

        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
            response ->  textView.text = response
        },
            Response.ErrorListener { textView.text = "ERROR" })

        queue.add(stringRequest)
    }

    private fun calculateHash(timestamp: String, publicKey: String, privateKey: String): String {
        val md: MessageDigest = MessageDigest.getInstance("MD5")
        val fullString = timestamp + privateKey + publicKey
        md.update(fullString.toByteArray())
        val digest: ByteArray = md.digest()

        return digest.joinToString("") {
            "%02x".format(it)
        }
    }
}
