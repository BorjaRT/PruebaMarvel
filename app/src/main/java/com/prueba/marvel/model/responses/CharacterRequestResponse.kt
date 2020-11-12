package com.prueba.marvel.model.responses

import com.google.gson.Gson
import com.prueba.marvel.model.CharacterListData

data class CharacterRequestResponse (val code: Int, val status: String, val copyright: String
                                     , val attributionText: String, val attributionHTML: String
                                     , val etag: String, val data: CharacterListData) {

    fun toJSONString(): String? {
        val gson = Gson()
        return gson.toJson(this)
    }
}