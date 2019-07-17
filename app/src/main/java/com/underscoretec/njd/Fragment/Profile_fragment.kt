package com.underscoretec.njd.Fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.underscoretec.njd.Adapters.CustomAdapter
import com.underscoretec.njd.Models.Likes
import com.underscoretec.njd.Models.Model_post
import com.underscoretec.njd.R
import org.json.JSONObject
import java.util.*


// TODO: Rename parameter arguments, choose names that match


class Profile_fragment : Fragment() {

    lateinit var recycler_view1: RecyclerView
    lateinit var textview_username: TextView
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.profile_fragment, container, false)


//        var colllaps = view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
//        colllaps.title = "Collapsing"

        initUiElements(view)
        onTap()
        getValue()


        val user = ArrayList<Model_post>()
        var likeList = ArrayList<Likes>()

        user.add(
            Model_post(
                "54545",
                "https://cloudflarestream.com/94416c1f102de9ce5d9e8d84758f9ae8/thumbnails/thumb_5_0.jpg",
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019.....",
                "url",
                0,
                0
            )
        )

       user.add(
            Model_post(
                "54545",
                "https://cloudflarestream.com/94416c1f102de9ce5d9e8d84758f9ae8/thumbnails/thumb_5_0.jpg",
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019.....",
                "url",
                0,
                0
            )
        )

       user.add(
            Model_post(
                "54545",
                "https://cloudflarestream.com/94416c1f102de9ce5d9e8d84758f9ae8/thumbnails/thumb_5_0.jpg",
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019.....",
                "url",
                0,
                0
            )
        )

       user.add(
            Model_post(
                "54545",
                "https://cloudflarestream.com/94416c1f102de9ce5d9e8d84758f9ae8/thumbnails/thumb_5_0.jpg",
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019.....",
                "url",
                0,
                0
            )
        )

       user.add(
            Model_post(
                "54545",
                "https://cloudflarestream.com/94416c1f102de9ce5d9e8d84758f9ae8/thumbnails/thumb_5_0.jpg",
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019.....",
                "url",
                0,
                0
            )
        )

       user.add(
            Model_post(
                "54545",
                "https://cloudflarestream.com/94416c1f102de9ce5d9e8d84758f9ae8/thumbnails/thumb_5_0.jpg",
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019.....",
                "url",
                0,
                0
            )
        )

       user.add(
            Model_post(
                "54545",
                "https://cloudflarestream.com/94416c1f102de9ce5d9e8d84758f9ae8/thumbnails/thumb_5_0.jpg",
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019.....",
                "url",
                0,
                0
            )
        )

       user.add(
            Model_post(
                "54545",
                "https://cloudflarestream.com/94416c1f102de9ce5d9e8d84758f9ae8/thumbnails/thumb_5_0.jpg",
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019.....",
                "url",
                0,
                0
            )
        )




        val adapter = CustomAdapter(user, context)

        recycler_view1.adapter = adapter

        return view
    }

    //method to handle onclicks
    private fun onTap() {

    }

    //method to get value from shared Preference
    private fun getValue() {
        sharedPref = context!!.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userData = sharedPref.getString("store", "")
        val jsonData = JSONObject(userData)
        val jsonResult = jsonData.getJSONObject("result")
        val userName = jsonResult.getString("userName")
        val email = jsonResult.getString("email")

        val enabled = jsonResult.getString("enabled")

        println("name $userName")
        println("email $email")

        println("enabled $enabled")
        setData(userName)

    }

    //method to set values in view that fetched from shared preference
    private fun setData(userName: String?) {
        textview_username.text = userName

    }

    //method to link views
    private fun initUiElements(view: View) {
        recycler_view1 = view.findViewById(R.id.recycler_content1)
        textview_username = view.findViewById(R.id.username)
        recycler_view1.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }
}
