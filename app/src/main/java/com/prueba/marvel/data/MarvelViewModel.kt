package com.prueba.marvel.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.prueba.marvel.model.CharacterResult
import com.prueba.marvel.model.Item
import com.prueba.marvel.model.responses.CharacterRequestResponse


class MarvelViewModel : ViewModel() {

    var mutableCharacterList: MutableLiveData<ArrayList<CharacterResult>>? = null
    var characterDetail: MutableLiveData<CharacterResult>? = null
    lateinit var filteredCharacterList: ArrayList<CharacterResult>
    var currentOffset: Int = 0

    fun init() {
        mutableCharacterList = MutableLiveData()
        characterDetail = MutableLiveData()
    }

    fun processCharacterListResponse(response: String){
        val responseObject = Gson().fromJson(response, CharacterRequestResponse::class.java)
        if(currentOffset == 0){
            mutableCharacterList!!.value = responseObject.data.results
        }else{
            var characters = mutableCharacterList!!.value
            characters!!.addAll(responseObject.data.results)
            mutableCharacterList!!.value = characters

//            mutableCharacterList!!.value!!.addAll(responseObject.data.results)
        }
        currentOffset = mutableCharacterList!!.value!!.size
    }

    fun processCharacterDetailResponse(response: String){
        val responseObject = Gson().fromJson(response, CharacterRequestResponse::class.java)
        characterDetail!!.value = responseObject.data.results[0]
    }

    fun getComicsAndStories() : ArrayList<Item> {
        val listItems: ArrayList<Item> = ArrayList()

        if(!characterDetail!!.value!!.comics!!.items.isNullOrEmpty()){
            listItems.addAll(characterDetail!!.value!!.comics!!.items!!)
        }
        if(!characterDetail!!.value!!.series!!.items.isNullOrEmpty()){
            listItems.addAll(characterDetail!!.value!!.series!!.items!!)
        }
        if(!characterDetail!!.value!!.stories!!.items.isNullOrEmpty()){
            listItems.addAll(characterDetail!!.value!!.stories!!.items!!)
        }
        if(!characterDetail!!.value!!.events!!.items.isNullOrEmpty()){
            listItems.addAll(characterDetail!!.value!!.events!!.items!!)
        }

        return listItems
    }
}