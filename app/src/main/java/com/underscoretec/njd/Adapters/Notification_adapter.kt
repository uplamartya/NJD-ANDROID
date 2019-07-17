package com.underscoretec.njd.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.underscoretec.njd.Models.Notification
import com.underscoretec.njd.R

class Notification_adapter (val notify:ArrayList<Notification>):
    RecyclerView.Adapter<Notification_adapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userimage = itemView.findViewById(R.id.notification_user_image) as ImageView
        val notification_content = itemView.findViewById(R.id.notification_description) as TextView
        val senders_name = itemView.findViewById(R.id.notification_text) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_view,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notify.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val userItem = notify[position]

        holder.userimage.setImageResource(notify.get(position).img)
        holder.notification_content.text = userItem.notification_content
        holder.senders_name.text = userItem.senders_name

    }
}