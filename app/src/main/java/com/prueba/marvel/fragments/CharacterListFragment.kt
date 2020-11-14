package com.prueba.marvel.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.prueba.marvel.R
import com.prueba.marvel.adapters.CharacterListAdapter
import com.prueba.marvel.interfaces.CharacterListListener
import com.prueba.marvel.model.CharacterResult

class CharacterListFragment : Fragment(){

    var characterListAdapter: CharacterListAdapter? = null
    var fragmentView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.fragment_character_list, container, false)
        return fragmentView!!
    }

    fun initializeData(characters: ArrayList<CharacterResult>, listener: CharacterListListener){
        characterListAdapter = CharacterListAdapter(characters, LayoutInflater.from(context), listener)
        fragmentView!!.findViewById<RecyclerView>(R.id.rv_character_list).adapter = characterListAdapter
    }
}