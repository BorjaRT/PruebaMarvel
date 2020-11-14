package com.prueba.marvel.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.prueba.marvel.BuildConfig
import com.prueba.marvel.R
import com.prueba.marvel.adapters.CharacterListAdapter
import com.prueba.marvel.interfaces.CharacterListListener
import com.prueba.marvel.model.Constants
import com.prueba.marvel.model.responses.CharacterRequestResponse
import java.security.MessageDigest


class MainActivity : AppCompatActivity(), CharacterListListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        characterListRequest()

    }

    private fun characterListRequest() {

        val queue = Volley.newRequestQueue(this)
        var baseUrl = "https://gateway.marvel.com:443/v1/public/characters?"

        baseUrl = baseUrl + "ts=1&" +
                "apikey=" + BuildConfig.API_KEY +
                "&hash=" + calculateHash("1",
            BuildConfig.API_KEY,
            BuildConfig.PRIVATE_KEY
        )

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
                "&hash=" + calculateHash("1",
            BuildConfig.API_KEY,
            BuildConfig.PRIVATE_KEY
        )

        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
                response ->  onCharacterResponse(response)
        },
            Response.ErrorListener {
                requestError(it.networkResponse.statusCode)
            }
        )

        queue.add(stringRequest)
    }

    private fun processResponse(response: String){

        val responseObject = Gson().fromJson(response, CharacterRequestResponse::class.java)
        val characterListAdapter = CharacterListAdapter(responseObject.data.results, LayoutInflater.from(this), this)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        var rvCharacters = findViewById<RecyclerView>(R.id.rv_character_list)
        rvCharacters.layoutManager = layoutManager
        rvCharacters.adapter = characterListAdapter


//        val textView = findViewById<TextView>(R.id.tv_result)
//        textView.text = responseObject.toJSONString()
    }

    private fun onCharacterResponse(response: String){
        val responseObject = Gson().fromJson(response, CharacterRequestResponse::class.java)

        val intent = Intent(this@MainActivity, CharacterActivity::class.java)
        intent.putExtra(Constants.EXTRA_CHARACTER, responseObject.data.results[0])
        startActivity(intent)
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

    override fun onCharacterSelected(characterId: String?) {
        Toast.makeText(this, characterId, Toast.LENGTH_SHORT).show()
    }
}
