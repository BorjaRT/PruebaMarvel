package com.prueba.marvel.data

class Constants {
    companion object {
        const val CHARACTER_LIST_INITIAL_REQUEST = "https://gateway.marvel.com:443/v1/public/characters?ts=%s&apikey=%s&hash=%s&limit=%s"
        const val CHARACTER_LIST_REQUEST = "https://gateway.marvel.com:443/v1/public/characters?ts=%s&apikey=%s&hash=%s&limit=%s&offset=%s"
        const val CHARACTER_DETAIL_REQUEST = "https://gateway.marvel.com:443/v1/public/characters/%s?ts=%s&apikey=%s&hash=%s"
        const val CHARACTER_NAME_SEARCH_REQUEST = "https://gateway.marvel.com:443/v1/public/characters?ts=%s&apikey=%s&hash=%s&limit=100&nameStartsWith=%s"
        const val CHARACTER_NAME_SEARCH_PAGE_REQUEST = "https://gateway.marvel.com:443/v1/public/characters?ts=%s&apikey=%s&hash=%s&limit=100&offset=%s&nameStartsWith=%s"
        const val LIST_REQUEST_STARTING_LIMIT = 40
        const val LIST_REQUEST_OVERSCROLL_LIMIT = 20
        const val LIST_CALL_MAX_LIMIT = 100
    }
}