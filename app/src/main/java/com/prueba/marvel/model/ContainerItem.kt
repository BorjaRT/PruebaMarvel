package com.prueba.marvel.model

import java.util.ArrayList

data class ContainerItem (val available: Int, val collectionURI: String
                          , val items: ArrayList<Item>, val returned: Int){
}