package com.prueba.marvel.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
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
import com.prueba.marvel.model.Constants.Companion.CHARACTER_LIST_INITIAL_REQUEST
import com.prueba.marvel.model.Constants.Companion.CHARACTER_LIST_REQUEST
import com.prueba.marvel.model.Constants.Companion.LIST_REQUEST_OVERSCROLL_LIMIT
import com.prueba.marvel.model.Constants.Companion.LIST_REQUEST_STARTING_LIMIT
import java.security.MessageDigest
import java.util.*


class MainActivity : AppCompatActivity(), CharacterListListener {

    lateinit var viewModel : MarvelViewModel
    lateinit var progressBar: ContentLoadingProgressBar
    lateinit var tvMessage: TextView
    lateinit var characterListAdapter: CharacterListAdapter
    lateinit var rvCharacters: RecyclerView

    var filterAvailable = false
    var filtered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_filter -> {
                if(filterAvailable) showFilterPanel()
                true
            }
            R.id.action_search -> {
                //TODO
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadCharacters(characterList : ArrayList<CharacterResult>) {
        characterListAdapter = CharacterListAdapter(characterList
            , LayoutInflater.from(applicationContext), this@MainActivity)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
//        val rvCharacters = findViewById<RecyclerView>(R.id.rv_character_list)
        rvCharacters.layoutManager = layoutManager
        rvCharacters.adapter = characterListAdapter

        if(viewModel.currentOffset !=  LIST_REQUEST_STARTING_LIMIT){
            rvCharacters.scrollToPosition(viewModel.currentOffset-LIST_REQUEST_OVERSCROLL_LIMIT)
        }

        rvCharacters.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if(dy > 0) //check for scroll down
                {
                    var visibleItemCount = recyclerView.layoutManager!!.childCount
                    var totalItemCount = recyclerView.layoutManager!!.itemCount
                    var pastVisibleItems = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if ( (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        Toast.makeText(applicationContext, "end", Toast.LENGTH_SHORT).show()
                        loadMoreCharacters()
                    }
                }
            }
        })

        filterAvailable = true
        progressBar.hide()
    }

    private fun characterListRequest() {

        val queue = Volley.newRequestQueue(this)

        val baseUrl = String.format(
            CHARACTER_LIST_INITIAL_REQUEST, "1", BuildConfig.API_KEY
            , calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)
            , LIST_REQUEST_STARTING_LIMIT.toString()
        )

        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
                response ->  viewModel.processCharacterListResponse(response, this)
        },
            Response.ErrorListener {
                requestError(it.networkResponse.statusCode)
            }
        )

        queue.add(stringRequest)
    }

    private fun loadMoreCharacters(){
        progressBar.show()

        val queue = Volley.newRequestQueue(this)

        val baseUrl = String.format(
            CHARACTER_LIST_REQUEST, "1", BuildConfig.API_KEY
            , calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)
            , LIST_REQUEST_OVERSCROLL_LIMIT.toString(), viewModel.currentOffset.toString()
        )

        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
                response ->  viewModel.processCharacterListResponse(response, this)
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

    override fun onCharactersLoaded() {
        characterListAdapter = CharacterListAdapter(viewModel.characterList!!
            , LayoutInflater.from(applicationContext), this@MainActivity)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        rvCharacters.layoutManager = layoutManager
        rvCharacters.adapter = characterListAdapter
        (rvCharacters.adapter as CharacterListAdapter).notifyDataSetChanged()
        if(viewModel.currentOffset > LIST_REQUEST_STARTING_LIMIT) {
            rvCharacters.scrollToPosition(viewModel.scrollItemCount)
        }
        progressBar.hide()
        filterAvailable = true
    }

    private fun showFilterPanel(){
        findViewById<RelativeLayout>(R.id.ly_filter).visibility = View.VISIBLE
        findViewById<EditText>(R.id.et_filter_input).requestFocus()
        findViewById<ImageView>(R.id.b_close).setOnClickListener { hideFilterPanel() }
    }

    private fun hideFilterPanel(){
        findViewById<RelativeLayout>(R.id.ly_filter).visibility = View.GONE
    }

    private fun initUI(){
        progressBar = findViewById(R.id.pb_progress_bar)

        rvCharacters = findViewById<RecyclerView>(R.id.rv_character_list)
        rvCharacters.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if(dy > 0) //check for scroll down
                {
                    val visibleItemCount = recyclerView.layoutManager!!.childCount
                    val totalItemCount = recyclerView.layoutManager!!.itemCount
                    val pastVisibleItems = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if ( (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        filterAvailable = false
                        loadMoreCharacters()
                    }
                }
            }
        })

        tvMessage = findViewById(R.id.tv_message)

        findViewById<EditText>(R.id.et_filter_input).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if (filterAvailable) filterList(s.toString())
            }
        })
    }

    private fun filterList(filterString: String) {
//        var filterString = filterString
//        filterString = filterString.toLowerCase(Locale.getDefault())
        if (filterString.isNotEmpty()) {
            filtered = true
            viewModel.filteredCharacterList = ArrayList<CharacterResult>()
            for (character in viewModel.characterList!!) {
                if (character.name!!.toLowerCase(Locale.getDefault()).contains(filterString.toLowerCase(Locale.getDefault()))) {
                    viewModel.filteredCharacterList.add(character)
                }
            }
        } else {
            filtered = false
            viewModel.filteredCharacterList = viewModel.characterList!!
        }
        updateFilterResults()
    }

    private fun updateFilterResults() {
        characterListAdapter.characters = viewModel.filteredCharacterList
        characterListAdapter.notifyDataSetChanged()
        if(viewModel.filteredCharacterList.isNullOrEmpty()){
            findViewById<TextView>(R.id.tv_message).text = getString(R.string.message_no_filter)
            findViewById<TextView>(R.id.tv_message).visibility = View.VISIBLE
        }else{
            findViewById<TextView>(R.id.tv_message).visibility = View.GONE
        }
    }

    private fun initData(){
        progressBar.show()
        viewModel = of(this).get(MarvelViewModel::class.java)
        viewModel.init()

        characterListRequest()
    }
}
