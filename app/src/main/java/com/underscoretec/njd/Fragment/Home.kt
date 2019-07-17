package com.underscoretec.njd.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.underscoretec.njd.Adapters.CustomAdapter
import com.underscoretec.njd.Adapters.Custom_Adapter_category
import com.underscoretec.njd.Models.Model_post
import com.underscoretec.njd.Models.Model_video_category
import com.underscoretec.njd.R
import java.util.ArrayList


// TODO: Rename parameter arguments, choose names that match

class Home : Fragment() {

    private lateinit var recycler_view: RecyclerView
    lateinit var recycler_content: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        recycler_view = view.findViewById(R.id.recycler_content_video)
        recycler_content = view.findViewById(R.id.recycler_content)

        itemcontent()

        recycler_view.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        val user = ArrayList<Model_post>()

        user.add(Model_post(R.drawable.dummypost, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.video,
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.userimage,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )
        user.add(
            Model_post(
                R.drawable.dummypost,
                R.drawable.user,
                "Now Just Dance All Performance World of Dance 2019....."
            )
        )

        val adapter = CustomAdapter(user,context)

        recycler_view.adapter = adapter

        return view
    }


    private fun itemcontent(){

        recycler_content.layoutManager = LinearLayoutManager(context,LinearLayout.HORIZONTAL,false)

        val user1 = ArrayList<Model_video_category>()

        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))
        user1.add(Model_video_category(R.drawable.balance_break, "Balance Break"))




        val  adapter1 = Custom_Adapter_category(user1)
        recycler_content.adapter = adapter1
//        recycler_content.setItemViewCacheSize(20)
//        recycler_content.setHasFixedSize(true)

    }


}
