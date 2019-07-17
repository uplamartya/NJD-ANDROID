package com.underscoretec.njd.Fragment


import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.underscoretec.njd.Adapters.CustomAdapter
import com.underscoretec.njd.Adapters.Custom_Adapter_category
import com.underscoretec.njd.Helpers.MySingleton
import com.underscoretec.njd.Models.Likes
import com.underscoretec.njd.Models.Model_post
import com.underscoretec.njd.Models.Model_video_category
import com.underscoretec.njd.R
import com.underscoretec.njd.Utility.SDUtility
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


// TODO: Rename parameter arguments, choose names that match

class Home : Fragment() {
    val user = ArrayList<Model_post>()
    private lateinit var recycler_view: RecyclerView
    lateinit var recycler_content: RecyclerView
    internal var mQueue: RequestQueue? = null
    val TAG = "CREATEPOSTACTIVITY"
    var likeList = ArrayList<Likes>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        initUiElements(view)

        fetchPost()
        itemcontent()



        return view
    }

    private fun initUiElements(view: View) {
        mQueue = MySingleton.getInstance(context!!).requestQueue
        recycler_view = view.findViewById(R.id.recycler_content_video)
        recycler_content = view.findViewById(R.id.recycler_content)
    }

    private fun fetchPost() {

        if (SDUtility.isNetworkAvailable(context)) {
//            showProgressBar(my_login_progressbar)
//            disableView()

            val url = "https://njd.api.underscoretec.com/feed/post"
            val jsonObjReq = object : JsonObjectRequest(
                Method.GET, url, null, Response.Listener { response ->
                    //                    hideProgressBar(my_login_progressbar)
//                    enableView()
                    Log.v("Response Fetching Feeds", response.toString())


                    try {
                        val serverResp = JSONObject(response.toString())
                        println("success result: $serverResp")
                        SDUtility.displayExceptionMessage(serverResp.getString("message"), context)
                        val errorStatus = serverResp.getString("error")
                        if (errorStatus == "true") {
                            val errorMessage = serverResp.getString("message")
                            SDUtility.displayExceptionMessage(errorMessage, context)
                        } else {
                            val resultObject = serverResp.getJSONArray("result")
                            for (i in 0 until resultObject.length()) {
                                var Post = resultObject.getJSONObject(i)

                                var likesArray = Post!!.getJSONArray("claps")

                                if (likesArray!!.length() > 0) {

                                    for (j in 0..(likesArray.length() - 1)) {

                                        likeList.add(Likes(likesArray.getString(j)))
                                    }
                                }
                                initializePostData(Post, likeList)
                            }
                            initializePostAdapter()

                        }
                    } catch (e: JSONException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                        SDUtility.displayExceptionMessage(e.message, context)
                    }
                },
                Response.ErrorListener { error ->
                    //                    hideProgressBar(my_login_progressbar)
//                    enableView()
                    VolleyLog.e("Error in Fetching Feeds", "Error" + error.message)
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    return headers
                }
            }
            jsonObjReq.tag = TAG
            mQueue!!.add(jsonObjReq)
        } else {

//            enableView()
            val mysnackbar = Snackbar.make(
                my_coordinator, "You are not connected to the Internet,Please check your Internet Connection",
                Snackbar.LENGTH_LONG
            )
            mysnackbar.setAction("Retry", View.OnClickListener {
                //                dosingin(data)
            })
            mysnackbar.show()
        }

    }

    private fun initializePostAdapter() {

        recycler_view.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)


        val adapter = CustomAdapter(user, context)

        recycler_view.adapter = adapter
    }

    private fun initializePostData(
        post: JSONObject,
        likeList: ArrayList<Likes>
    ) {

        user.add(
            Model_post(
                post.getString("_id"),
                post.getString("thumbNail"),
                R.drawable.userimage,
                if (post.has("text")) post.getString("text") else "No Video Title Found",
                post.getString("videoUrl"),
                likeList,
                post.getInt("comments")

            )
        )

    }


    private fun itemcontent() {

        recycler_content.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)

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


        val adapter1 = Custom_Adapter_category(user1)
        recycler_content.adapter = adapter1
//        recycler_content.setItemViewCacheSize(20)
//        recycler_content.setHasFixedSize(true)

    }


}
