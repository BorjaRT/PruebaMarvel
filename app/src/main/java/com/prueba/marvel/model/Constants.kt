package com.prueba.marvel.model

class Constants {
    companion object {
        const val EXTRA_CHARACTER = "CharacterInfo"
        const val CHARACTER_LIST_INITIAL_REQUEST = "https://gateway.marvel.com:443/v1/public/characters?ts=%s&apikey=%s&hash=%s&limit=%s"
        const val CHARACTER_LIST_REQUEST = "https://gateway.marvel.com:443/v1/public/characters?ts=%s&apikey=%s&hash=%s&limit=%s&offset=%s"
        const val CHARACTER_DETAIL_REQUEST = "https://gateway.marvel.com:443/v1/public/characters/%s?ts=%s&apikey=%s&hash=%s"
        const val LIST_REQUEST_STARTING_LIMIT = 40
        const val LIST_REQUEST_OVERSCROLL_LIMIT = 10
    }
}