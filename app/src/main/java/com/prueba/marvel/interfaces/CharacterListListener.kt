package com.prueba.marvel.interfaces

interface CharacterListListener {
    fun onCharacterSelected(characterId: String?)
    fun onCharactersLoaded(scroll: Boolean)
    fun onCharacterSearchComplete()
}