package com.prueba.marvel.model

class Constants {
    companion object {
        val EXTRA_CHARACTER = "CharacterInfo"
        val CHARACTER_LIST_REQUEST = "https://gateway.marvel.com:443/v1/public/characters?ts=%s&apikey=%s&hash=%s"
    }
}