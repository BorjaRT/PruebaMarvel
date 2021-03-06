package com.prueba.marvel.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CharacterResult (val id: String?, val name: String?, val description: String?
                            , val modified: String?, val thumbnail: Thumbnail?
                            , val resourceURI: String?, val comics: ContainerItem?
                            , val series: ContainerItem?, val stories: ContainerItem?
                            , val events: ContainerItem?, val urls: ArrayList<Url>?) : Parcelable