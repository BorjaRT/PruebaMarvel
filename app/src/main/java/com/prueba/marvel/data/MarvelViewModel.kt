package com.prueba.marvel.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.prueba.marvel.interfaces.CharacterListListener
import com.prueba.marvel.model.CharacterResult
import com.prueba.marvel.model.Constants.Companion.LIST_CALL_MAX_LIMIT
import com.prueba.marvel.model.Item
import com.prueba.marvel.model.responses.CharacterRequestResponse


class MarvelViewModel : ViewModel() {

    var characterList: ArrayList<CharacterResult>? = null
    var characterDetail: MutableLiveData<CharacterResult>? = null
    lateinit var filteredCharacterList: ArrayList<CharacterResult>
    var searchCharacterList: ArrayList<CharacterResult> = ArrayList<CharacterResult>()

    var currentOffset: Int = 0
    var totalCharacters: Int = 0
    var scrollItemCount: Int = 0
    var totalSearchCharacters: Int = 0
    var currentSearchOffset: Int = 0
    lateinit var searchString: String

    fun init() {
        characterDetail = MutableLiveData()
    }

    fun processCharacterListResponse(response: String, listener: CharacterListListener){
        val responseObject = Gson().fromJson(response, CharacterRequestResponse::class.java)
        if(currentOffset == 0){
            characterList = responseObject.data.results
            totalCharacters = responseObject.data.total
        }else{
            scrollItemCount = characterList!!.size
            characterList!!.addAll(responseObject.data.results)
        }
        currentOffset = characterList!!.size
        listener.onCharactersLoaded(true)
    }

    fun processCharacterNameSearchResponse(response: String, listener: CharacterListListener){
        val responseObject = Gson().fromJson(response, CharacterRequestResponse::class.java)
        if(currentSearchOffset == 0){
            searchCharacterList = responseObject.data.results
            totalSearchCharacters = responseObject.data.total
        }else{
            searchCharacterList.addAll(responseObject.data.results)
        }
        currentSearchOffset += LIST_CALL_MAX_LIMIT
        listener.onCharacterSearchComplete()
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