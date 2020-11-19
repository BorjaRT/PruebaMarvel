package com.prueba.marvel.model

data class CharacterListData (val offset: Int, val limit: Int, val total: Int, val count: Int
                              , val results: ArrayList<CharacterResult>)