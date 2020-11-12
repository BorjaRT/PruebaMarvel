package com.prueba.marvel.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class ContainerItem (val available: Int?, val collectionURI: String?
                          , val items: ArrayList<Item>?, val returned: Int?): Parcelable{
}