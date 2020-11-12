package com.prueba.marvel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.prueba.marvel.model.responses.CharacterRequestResponse
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnListRequest = findViewById<Button>(R.id.btn_list_request)
        btnListRequest.setOnClickListener { characterListRequest() }
        val btnCharacterRequest = findViewById<Button>(R.id.btn_character_request)
        btnCharacterRequest.setOnClickListener { characterRequest(findViewById<EditText>(R.id.et_name).text.toString()) }
    }

    private fun characterListRequest() {

        val queue = Volley.newRequestQueue(this)
        var baseUrl = "https://gateway.marvel.com:443/v1/public/characters?"

        baseUrl = baseUrl + "ts=1&" +
                "apikey=" + BuildConfig.API_KEY +
                "&hash=" + calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)

        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
            response ->  processResponse(response)
        },
            Response.ErrorListener {
                requestError(it.networkResponse.statusCode)
            }
        )

        queue.add(stringRequest)
    }

    private fun characterRequest(characterId: String){

        val queue = Volley.newRequestQueue(this)
        var baseUrl = "https://gateway.marvel.com:443/v1/public/characters/" + characterId + "?"

        baseUrl = baseUrl + "ts=1&" +
                "apikey=" + BuildConfig.API_KEY +
                "&hash=" + calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)

        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
                response ->  processResponse(response)
        },
            Response.ErrorListener {
                requestError(it.networkResponse.statusCode)
            }
        )

        queue.add(stringRequest)
    }

    private fun processResponse(response: String){

        val responseObject = Gson().fromJson(response, CharacterRequestResponse::class.java)
        val textView = findViewById<TextView>(R.id.tv_result)
        textView.text = responseObject.toJSONString()
    }

    private fun requestError(statusCode: Int){
        findViewById<TextView>(R.id.tv_result).text = "ERROR:" + statusCode
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
