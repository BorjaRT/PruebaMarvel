package com.prueba.marvel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.prueba.marvel.R
import com.prueba.marvel.data.MarvelViewModel
import com.prueba.marvel.model.CharacterResult

class CharacterDetailFragment : Fragment {

    lateinit var viewModel : MarvelViewModel
    var fragmentView: View? = null

    constructor(viewModel: MarvelViewModel) : super() {
        this.viewModel = viewModel
        viewModel.characterDetail!!.observe(this, Observer<Any?> {
            loadCharacterData(viewModel.characterDetail!!.value!!)
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

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//    }

    private fun loadCharacterData(characterData : CharacterResult){
        fragmentView!!.findViewById<TextView>(R.id.tv_name).text = characterData.name
        fragmentView!!.findViewById<TextView>(R.id.tv_description).text = characterData.description
        val url = characterData.thumbnail!!.path + "." + characterData.thumbnail.extension

        val image = fragmentView!!.findViewById<WebView>(R.id.wv_image)
        image.settings.loadWithOverviewMode = true
        image.settings.useWideViewPort = true
        image.loadUrl(url)
    }

}