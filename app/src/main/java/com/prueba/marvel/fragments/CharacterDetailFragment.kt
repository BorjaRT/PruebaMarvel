package com.prueba.marvel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prueba.marvel.R
import com.prueba.marvel.adapters.ComicsListAdapter
import com.prueba.marvel.data.MarvelViewModel
import com.prueba.marvel.interfaces.CharacterListener
import com.prueba.marvel.model.CharacterResult
import com.prueba.marvel.model.Item

class CharacterDetailFragment : Fragment, CharacterListener {

    var fragmentView: View? = null
//    lateinit var progressBar: ContentLoadingProgressBar

    constructor(viewModel: MarvelViewModel) : super() {
        viewModel.characterDetail!!.observe(this, Observer<Any?> {
            loadCharacterData(viewModel.characterDetail!!.value!!, viewModel.getComicsAndStories())
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.fragment_character_detail, container, false)
        return fragmentView!!
    }

    private fun loadCharacterData(characterData : CharacterResult, comicsEvents: ArrayList<Item>){

        val tvDescription = fragmentView!!.findViewById<TextView>(R.id.tv_description)
        val tvDescriptionHeader = fragmentView!!.findViewById<TextView>(R.id.tv_description_header)
        val tvComicsHeader = fragmentView!!.findViewById<TextView>(R.id.tv_comics_header)
        val rvComicsList = fragmentView!!.findViewById<RecyclerView>(R.id.rv_comics_list)

        fragmentView!!.findViewById<TextView>(R.id.tv_name).text = characterData.name

        if(characterData.description.isNullOrEmpty()){
            tvDescription.visibility = View.GONE
            tvDescriptionHeader.visibility = View.GONE
        }else{
            tvDescription.visibility = View.VISIBLE
            tvDescriptionHeader.visibility = View.VISIBLE
            tvDescription.text = characterData.description
        }

        if(comicsEvents.isNullOrEmpty()){
            tvComicsHeader.visibility = View.VISIBLE
            rvComicsList.visibility = View.VISIBLE
        }else{
            tvComicsHeader.visibility = View.VISIBLE
            rvComicsList.visibility = View.VISIBLE
            val comicsListAdapter = ComicsListAdapter(
                comicsEvents
                , LayoutInflater.from(context), this)
            val comicsLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            rvComicsList.layoutManager = comicsLayoutManager
            rvComicsList.adapter = comicsListAdapter
        }

        val url = characterData.thumbnail!!.path + "." + characterData.thumbnail.extension

        val image = fragmentView!!.findViewById<WebView>(R.id.wv_image)
        image.settings.loadWithOverviewMode = true
        image.settings.useWideViewPort = true
        image.loadUrl(url)

        fragmentView!!.findViewById<ContentLoadingProgressBar>(R.id.pb_progress_bar).hide()
    }

    override fun onComicSelected(resource: String) {
        Toast.makeText(context, resource, Toast.LENGTH_SHORT).show()
    }

    override fun onSeriesSelected(resource: String) {
        Toast.makeText(context, resource, Toast.LENGTH_SHORT).show()
    }

    override fun onEventSelected(resource: String) {
        Toast.makeText(context, resource, Toast.LENGTH_SHORT).show()
    }

    override fun onStorySelected(resource: String) {
        Toast.makeText(context, resource, Toast.LENGTH_SHORT).show()
    }

}