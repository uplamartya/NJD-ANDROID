package com.underscoretec.njd.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.underscoretec.njd.Models.Model_video_category
import com.underscoretec.njd.R


class Custom_Adapter_category (val video_category:ArrayList<Model_video_category>):
    RecyclerView.Adapter<Custom_Adapter_category.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemimage1 = itemView.findViewById(R.id.my_item_image) as ImageView
        val itemname1 = itemView.findViewById(R.id.my_item_name) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_category_items,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return video_category.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userItem = video_category[position]

        holder.itemimage1.setImageResource(video_category[position].itemimage)
        holder.itemname1.text = userItem.itemname

    }
}