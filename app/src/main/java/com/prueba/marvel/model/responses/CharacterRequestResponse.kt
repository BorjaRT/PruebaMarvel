package com.prueba.marvel.model.responses

import com.prueba.marvel.model.CharacterListData

data class CharacterRequestResponse (val code: Int, val status: String, val copyright: String
                                     , val attributionText: String, val attributionHTML: String
                                     , val etag: String, val data: CharacterListData) {
}