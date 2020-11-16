package com.prueba.marvel.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prueba.marvel.R
import com.prueba.marvel.interfaces.CharacterListener
import com.prueba.marvel.model.ContainerItem
import com.prueba.marvel.model.Item

class StoriesListAdapter (
    var stories: ArrayList<Item>,
    var inflater: LayoutInflater?,
    var listener: CharacterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView: View = inflater!!.inflate(R.layout.item_detail, parent, false)
        return ItemViewHolder(rowView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val story: Item = stories[position]
        val itemViewHolder = viewHolder as ItemViewHolder
        itemViewHolder.tvName.text = story.name
        viewHolder.itemView.setOnClickListener { listener.onStorySelected(story.resourceURI!!) }
    }

    override fun getItemId(position: Int): Long {
        return stories[position].hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    internal class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
    }
}