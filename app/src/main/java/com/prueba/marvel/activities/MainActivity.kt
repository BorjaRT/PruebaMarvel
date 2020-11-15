package com.prueba.marvel.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.prueba.marvel.BuildConfig
import com.prueba.marvel.R
import com.prueba.marvel.adapters.CharacterListAdapter
import com.prueba.marvel.data.MarvelViewModel
import com.prueba.marvel.fragments.CharacterDetailFragment
import com.prueba.marvel.interfaces.CharacterListListener
import com.prueba.marvel.model.CharacterResult
import com.prueba.marvel.model.Constants.Companion.CHARACTER_DETAIL_REQUEST
import com.prueba.marvel.model.Constants.Companion.CHARACTER_LIST_REQUEST
import java.security.MessageDigest


class MainActivity : AppCompatActivity(), CharacterListListener {

    lateinit var viewModel : MarvelViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = of(this).get(MarvelViewModel::class.java)
        viewModel.init()

        viewModel.mutableCharacterList!!.observe(this, Observer<Any?> {
            loadCharacters(viewModel.mutableCharacterList!!.value!!)
        })

        characterListRequest()
    }

    private fun loadCharacters(characterList : ArrayList<CharacterResult>) {
        val characterListAdapter = CharacterListAdapter(
            characterList
            , LayoutInflater.from(applicationContext), this@MainActivity)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        val rvCharacters = findViewById<RecyclerView>(R.id.rv_character_list)
        rvCharacters.layoutManager = layoutManager
        rvCharacters.adapter = characterListAdapter
    }

    private fun characterListRequest() {

        val queue = Volley.newRequestQueue(this)

        val baseUrl = String.format(
            CHARACTER_LIST_REQUEST, "1", BuildConfig.API_KEY
            , calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)
        )

        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
                response ->  viewModel.processCharacterListResponse(response)
        },
            Response.ErrorListener {
                requestError(it.networkResponse.statusCode)
            }
        )

        queue.add(stringRequest)
    }

    private fun characterRequest(characterId: String){

        val queue = Volley.newRequestQueue(this)

        val baseUrl = String.format(
            CHARACTER_DETAIL_REQUEST, characterId, "1", BuildConfig.API_KEY
            , calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)
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

    private fun onCharacterResponse(response: String){

        viewModel.processCharacterDetailResponse(response)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = CharacterDetailFragment(viewModel)
        fragmentTransaction.add(R.id.fl_detail, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun requestError(statusCode: Int){
        Toast.makeText(this, "ERROR:" + statusCode, Toast.LENGTH_SHORT).show()
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
        characterRequest(characterId!!)
    }
}
