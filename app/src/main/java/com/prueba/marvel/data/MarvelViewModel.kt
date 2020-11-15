package com.prueba.marvel.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.prueba.marvel.model.CharacterResult
import com.prueba.marvel.model.responses.CharacterRequestResponse


class MarvelViewModel : ViewModel() {

    var mutableCharacterList: MutableLiveData<ArrayList<CharacterResult>>? = null
    var characterDetail: MutableLiveData<CharacterResult>? = null

    fun init() {
        mutableCharacterList = MutableLiveData()
        characterDetail = MutableLiveData()
    }

    fun processCharacterListResponse(response: String){
        val responseObject = Gson().fromJson(response, CharacterRequestResponse::class.java)
        mutableCharacterList!!.value = responseObject.data.results
    }

    fun processCharacterDetailResponse(response: String){
        val responseObject = Gson().fromJson(response, CharacterRequestResponse::class.java)
        characterDetail!!.value = responseObject.data.results[0]
    }
}