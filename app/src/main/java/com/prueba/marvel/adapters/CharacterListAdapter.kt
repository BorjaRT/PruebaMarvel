package com.prueba.marvel.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prueba.marvel.R
import com.prueba.marvel.interfaces.CharacterListListener
import com.prueba.marvel.model.CharacterResult
import kotlin.collections.ArrayList

class CharacterListAdapter(
    var characters: ArrayList<CharacterResult>,
    var inflater: LayoutInflater?,
    var listener: CharacterListListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private var characters: ArrayList<CharacterResult>? = null
//    private var inflater: LayoutInflater? = null
//    private var listener: CharacterListListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView: View = inflater!!.inflate(R.layout.item_charater_list, parent, false)
        return ItemViewHolder(rowView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val character: CharacterResult = characters!!.get(position)
        val itemViewHolder = viewHolder as ItemViewHolder
        itemViewHolder.tvName.text = character.name
        viewHolder.itemView.setOnClickListener { listener!!.onCharacterSelected(character.id) }
    }

    override fun getItemId(position: Int): Long {
        return characters!![position].hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return characters!!.size
    }

    internal class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
    }
}