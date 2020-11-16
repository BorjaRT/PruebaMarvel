package com.prueba.marvel.interfaces

interface CharacterListener {
    fun onComicSelected(resource : String)
    fun onSeriesSelected(resource : String)
    fun onEventSelected(resource : String)
    fun onStorySelected(resource : String)
}