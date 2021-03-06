package com.prueba.marvel.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProviders.of
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.prueba.marvel.BuildConfig
import com.prueba.marvel.R
import com.prueba.marvel.adapters.CharacterListAdapter
import com.prueba.marvel.data.Constants.Companion.CHARACTER_DETAIL_REQUEST
import com.prueba.marvel.data.Constants.Companion.CHARACTER_LIST_INITIAL_REQUEST
import com.prueba.marvel.data.Constants.Companion.CHARACTER_LIST_REQUEST
import com.prueba.marvel.data.Constants.Companion.CHARACTER_NAME_SEARCH_PAGE_REQUEST
import com.prueba.marvel.data.Constants.Companion.CHARACTER_NAME_SEARCH_REQUEST
import com.prueba.marvel.data.Constants.Companion.LIST_REQUEST_OVERSCROLL_LIMIT
import com.prueba.marvel.data.Constants.Companion.LIST_REQUEST_STARTING_LIMIT
import com.prueba.marvel.data.MarvelViewModel
import com.prueba.marvel.fragments.CharacterDetailFragment
import com.prueba.marvel.interfaces.CharacterListListener
import java.security.MessageDigest
import java.util.*


class MainActivity : AppCompatActivity(), CharacterListListener {

    private lateinit var viewModel : MarvelViewModel
    lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var tvMessage: TextView
    private lateinit var characterListAdapter: CharacterListAdapter
    private lateinit var rvCharacters: RecyclerView

    var filterAvailable = false
    private var searchAvailable = true
    private var filtered = false
    var loading = false
    var searching = false
    var showingDetail = false

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
        return when (item.itemId) {
            R.id.action_filter -> {
                if(filterAvailable && !showingDetail
                    && findViewById<RelativeLayout>(R.id.ly_filter).visibility == View.GONE) {
                    showFilterPanel()
                    hideSearchPanel()
                }else{
                    hideFilterPanel()
                }
                true
            }
            R.id.action_search -> {
                if(searchAvailable && !showingDetail
                    && findViewById<RelativeLayout>(R.id.ly_search).visibility == View.GONE) {
                    showSearchPanel()
                    hideFilterPanel()
                }else{
                    hideSearchPanel()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadCharacterList(requestString: String?) {

        val queue = Volley.newRequestQueue(this)

        var baseUrl = requestString
        if(baseUrl.isNullOrBlank()){
            baseUrl = String.format(
                CHARACTER_LIST_INITIAL_REQUEST, "1", BuildConfig.API_KEY
                , calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)
                , LIST_REQUEST_STARTING_LIMIT.toString()
            )
        }

        Log.i("MARVEL-REQUEST", baseUrl)
        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
                response ->  viewModel.processCharacterListResponse(response, this)
        },
            Response.ErrorListener {
                characterListRequestError(baseUrl)
            }
        )

        queue.add(stringRequest)
    }

    private fun loadCharactersPage(requestString: String?){
        progressBar.show()

        val queue = Volley.newRequestQueue(this)

        var baseUrl = requestString
        if(baseUrl.isNullOrBlank()){
            baseUrl = String.format(
                CHARACTER_LIST_REQUEST, "1", BuildConfig.API_KEY
                , calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)
                , LIST_REQUEST_OVERSCROLL_LIMIT.toString(), viewModel.currentOffset.toString()
            )
        }

        Log.i("MARVEL-REQUEST", baseUrl)
        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
                response ->  viewModel.processCharacterListResponse(response, this)
        },
            Response.ErrorListener {
                characterListPageRequestError(baseUrl)
            }
        )

        queue.add(stringRequest)
    }

    private fun characterRequest(characterId: String?, requestString: String?){
        loading = true
        filterAvailable = false
        progressBar.show()

        val queue = Volley.newRequestQueue(this)

        var baseUrl = requestString
        if(baseUrl.isNullOrBlank()){
            baseUrl = String.format(
                CHARACTER_DETAIL_REQUEST, characterId, "1", BuildConfig.API_KEY
                , calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)
            )
        }

        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
                response ->  onCharacterResponse(response)
        },
            Response.ErrorListener {
                characterDetailRequestError(baseUrl)
            }
        )

        queue.add(stringRequest)
    }

    private fun onCharacterResponse(response: String){

        progressBar.hide()
        viewModel.processCharacterDetailResponse(response)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = CharacterDetailFragment(viewModel)
        fragmentTransaction.add(R.id.fl_detail, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        showingDetail = true
        searchAvailable = true
        filterAvailable = true
        loading = false
    }

    private fun characterListRequestError(requestString: String?){
        Snackbar.make(findViewById(R.id.main_layout), getString(R.string.character_list_error)
            , Snackbar.LENGTH_LONG).setAction(getString(R.string.snack_retry)){
            progressBar.show()
            loadCharacterList(requestString)
        }.show()
        progressBar.hide()
    }

    private fun characterListPageRequestError(requestString: String?){
        Snackbar.make(findViewById(R.id.main_layout), getString(R.string.character_list_error)
            , Snackbar.LENGTH_LONG).setAction(getString(R.string.snack_retry)){
            progressBar.show()
            loadCharactersPage(requestString)
        }.show()
        progressBar.hide()
    }

    private fun characterDetailRequestError(requestString: String?){
        Snackbar.make(findViewById(R.id.main_layout), getString(R.string.character_detail_error)
            , Snackbar.LENGTH_LONG).setAction(getString(R.string.snack_retry)){
            progressBar.show()
            characterRequest(null, requestString)
        }.show()
        progressBar.hide()
    }

    private fun characterSearchRequestError(searchString: String){
        Snackbar.make(findViewById(R.id.main_layout), getString(R.string.character_list_error)
            , Snackbar.LENGTH_LONG).setAction(getString(R.string.snack_retry)){
            progressBar.show()
            doSearch(searchString)
        }.show()
        progressBar.hide()
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
        characterRequest(characterId!!, null)
    }

    override fun onCharactersLoaded(scroll: Boolean) {
        characterListAdapter = CharacterListAdapter(viewModel.characterList!!
            , LayoutInflater.from(applicationContext), this@MainActivity)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        rvCharacters.layoutManager = layoutManager
        rvCharacters.adapter = characterListAdapter
        (rvCharacters.adapter as CharacterListAdapter).notifyDataSetChanged()
        if(viewModel.currentOffset > LIST_REQUEST_STARTING_LIMIT && scroll) {
            rvCharacters.scrollToPosition(viewModel.scrollItemCount - 1)
        }
        progressBar.hide()
        filterAvailable = true
        searchAvailable = true
        loading = false
    }

    override fun onCharacterSearchComplete() {

        if(viewModel.currentSearchOffset >= viewModel.totalSearchCharacters){
            characterListAdapter = CharacterListAdapter(
                viewModel.searchCharacterList
                , LayoutInflater.from(applicationContext), this@MainActivity)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
            rvCharacters.layoutManager = layoutManager
            rvCharacters.adapter = characterListAdapter
            (rvCharacters.adapter as CharacterListAdapter).notifyDataSetChanged()
            if(viewModel.currentOffset > LIST_REQUEST_STARTING_LIMIT) {
                rvCharacters.scrollToPosition(viewModel.scrollItemCount - 1)
            }
            progressBar.hide()
            filterAvailable = true
            loading = false
        }else{
            doSearch(viewModel.searchString)
        }
    }

    private fun showFilterPanel(){
        findViewById<RelativeLayout>(R.id.ly_filter).visibility = View.VISIBLE
        findViewById<EditText>(R.id.et_filter_input).requestFocus()
        findViewById<ImageView>(R.id.b_clean).setOnClickListener {
            findViewById<EditText>(R.id.et_filter_input).text.clear()
        }
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(findViewById<EditText>(R.id.et_filter_input)
            , InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideFilterPanel(){
        findViewById<RelativeLayout>(R.id.ly_filter).visibility = View.GONE
        if(findViewById<RelativeLayout>(R.id.ly_search).visibility == View.GONE){
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(findViewById<EditText>(R.id.et_filter_input).windowToken, 0)
        }
    }

    private fun showSearchPanel(){
        findViewById<RelativeLayout>(R.id.ly_search).visibility = View.VISIBLE
        findViewById<EditText>(R.id.et_search_input).requestFocus()
        findViewById<ImageView>(R.id.b_search).setOnClickListener {
            doSearch(findViewById<EditText>(R.id.et_search_input).editableText.toString())
        }
        findViewById<ImageView>(R.id.b_clear_search).setOnClickListener {
            findViewById<EditText>(R.id.et_search_input).text.clear()
            searching = false
            onCharactersLoaded(false)
        }
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(findViewById<EditText>(R.id.et_search_input)
            , InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideSearchPanel(){
        findViewById<RelativeLayout>(R.id.ly_search).visibility = View.GONE
        if(findViewById<RelativeLayout>(R.id.ly_filter).visibility == View.GONE){
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(findViewById<EditText>(R.id.et_search_input).windowToken, 0)
        }
    }

    private fun initUI(){
        progressBar = findViewById(R.id.pb_progress_bar)

        rvCharacters = findViewById(R.id.rv_character_list)
        rvCharacters.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if(dy > 0)
                {
                    val visibleItemCount = recyclerView.layoutManager!!.childCount
                    val totalItemCount = recyclerView.layoutManager!!.itemCount
                    val pastVisibleItems = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if ( (visibleItemCount + pastVisibleItems) >= totalItemCount && !loading && !searching) {
                        progressBar.show()
                        loading = true
                        filterAvailable = false
                        loadCharactersPage(null)
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
        if (filterString.isNotEmpty()) {
            filtered = true
            viewModel.filteredCharacterList = ArrayList()
            for (character in viewModel.characterList!!) {
                if (character.name!!.toLowerCase(Locale.getDefault())
                        .contains(filterString.toLowerCase(Locale.getDefault()))) {
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
        loadCharacterList(null)
    }

    private fun doSearch(searchString: String){
        searching = true
        viewModel.searchString = searchString
        loading = true
        filterAvailable = false
        progressBar.show()

        if(viewModel.searchCharacterList.isNullOrEmpty()){
            viewModel.totalSearchCharacters = 0
            viewModel.currentSearchOffset = 0
        }

        val queue = Volley.newRequestQueue(this)

        val baseUrl: String
        if(viewModel.totalSearchCharacters == 0){
            baseUrl = String.format(
                CHARACTER_NAME_SEARCH_REQUEST, "1", BuildConfig.API_KEY
                , calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)
                , searchString
            )
        }else{
            baseUrl = String.format(
                CHARACTER_NAME_SEARCH_PAGE_REQUEST, "1", BuildConfig.API_KEY
                , calculateHash("1", BuildConfig.API_KEY, BuildConfig.PRIVATE_KEY)
                , viewModel.currentSearchOffset, searchString
            )
        }

        Log.i("MARVEL-REQUEST", baseUrl)
        val stringRequest = StringRequest(Request.Method.GET, baseUrl, Response.Listener<String> {
                response ->  viewModel.processCharacterNameSearchResponse(response, this)
        },
            Response.ErrorListener {
                characterSearchRequestError(searchString)
            }
        )

        queue.add(stringRequest)
    }
}
