package com.underscoretec.njd.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.underscoretec.njd.Models.Model_post
import android.content.Context
import com.bumptech.glide.Glide
import com.underscoretec.njd.Activities.DashboardActivity
import com.underscoretec.njd.R


class CustomAdapter(
    val userList: ArrayList<Model_post>,
    val context: Context?
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.post_items, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post: Model_post = userList[position]

        holder.describe.text = post.contentdescription
        Glide.with(context!!).load(post.contentimage).into(holder.imageviewContent)
        holder.imageviewuser.setImageResource(post.userimage)

        holder.relativeLayout.setOnClickListener {
            if (context is DashboardActivity) {
                context.openVideoDescFragment(post)
            }

        }


    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val imageviewContent = itemView.findViewById(R.id.content_image) as ImageView
        val imageviewuser = itemView.findViewById(R.id.user_image) as ImageView
        val describe = itemView.findViewById(R.id.content_description) as TextView
        val relativeLayout = itemView.findViewById(R.id.relative_video) as RelativeLayout
//

    }
}
