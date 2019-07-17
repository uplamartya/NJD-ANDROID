package com.underscoretec.njd.Fragment


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.underscoretec.njd.Adapters.CustomAdapter
import com.underscoretec.njd.Helpers.MySingleton
import com.underscoretec.njd.Models.Likes
import com.underscoretec.njd.Models.Model_post
import com.underscoretec.njd.R
import com.underscoretec.njd.Utility.SDUtility
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


@SuppressLint("ValidFragment")
class Video_description_fragment @SuppressLint("ValidFragment") constructor(private val post: Model_post) : Fragment() {
    lateinit var mPlayerView: SimpleExoPlayerView
    lateinit var mPlayer: SimpleExoPlayer
    lateinit var user: TextView
    lateinit var text_title: TextView
    lateinit var txt_clap: TextView
    lateinit var sharedPref: SharedPreferences
    lateinit var txt_comment: TextView
    lateinit var relatedVideo: RecyclerView
    lateinit var rl_clap: RelativeLayout
    private var userName: String = ""
    private var _id: String = ""
    internal var mQueue: RequestQueue? = null
    val TAG = "VideoDescFrag"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_video_description_fragment, container, false)
        initUiElements(view)
        getuserData()
        onTap()
        getPlayer()
        setVideoDesc()
        getValue()

        relatedVideo = view.findViewById(R.id.recycler_related_video)

        relatedVideo.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

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


        val adapter = CustomAdapter(user, context)
        relatedVideo.adapter = adapter
        relatedVideo.setItemViewCacheSize(20)
        relatedVideo.setHasFixedSize(true)

        return view
    }

    private fun onTap() {

        rl_clap.setOnClickListener {

            doClap(_id, post.postId, userName, 1)
        }

    }

    private fun getuserData() {
        sharedPref = context!!.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userData = sharedPref.getString("store", "")
        val jsonData = JSONObject(userData)
        val jsonResult = jsonData.getJSONObject("result")
        userName = jsonResult.getString("userName")
        _id = jsonResult.getString("_id")

    }

    private fun doClap(_id: String, postId: String, userName: String, clapCount: Int) {
        println("Do clap")

        if (SDUtility.isNetworkAvailable(context)) {

            val url =
                "https://njd.api.underscoretec.com/clap/post?&userId=$_id&pid=$postId&userName=$userName&claps=$clapCount"

            val jsonObjReq = object : JsonObjectRequest(
                Method.PUT, url, null, Response.Listener { response ->

                    Log.v("Response of Clap Post", response.toString())

                    //TODO store in sharedPreference

                    try {
                        val serverResp = JSONObject(response.toString())
                        println("success result: $serverResp")
                        //       SDUtility.displayExceptionMessage(serverResp.getString("message"), context)
                        val errorStatus = serverResp.getString("error")
                        if (errorStatus == "true") {
                            val errorMessage = serverResp.getString("message")
                            SDUtility.displayExceptionMessage(errorMessage, context)
                        } else {
                            txt_clap.text = serverResp.getString("totalClaps").toString()
                        }
                    } catch (e: JSONException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                        SDUtility.displayExceptionMessage(e.message, context)
                    }
                },
                Response.ErrorListener { error ->

                    VolleyLog.e("Error of Clap Post", "Error" + error.message)
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


            val mysnackbar = Snackbar.make(
                my_coordinator,
                "You are not connected to the Internet,Please check your Internet Connection",
                Snackbar.LENGTH_LONG
            )
            mysnackbar.setAction("Retry", View.OnClickListener {

            })
            mysnackbar.show()
        }

    }


    private fun initUiElements(view: View) {
        mQueue = MySingleton.getInstance(context!!).requestQueue
        mPlayerView = view.findViewById(R.id.player)
        user = view.findViewById(R.id.user_name)
        text_title = view.findViewById(R.id.text_title)
        txt_clap = view.findViewById(R.id.txt_clap)
        txt_comment = view.findViewById(R.id.txt_comment)
        rl_clap = view.findViewById(R.id.rl_clap)
    }

    private fun setVideoDesc() {
        text_title.text = post.contentdescription
        txt_comment.text = post.comment.toString()
        txt_clap.text = post.likes.toString()
    }

    private fun getPlayer() {
        // URL of the video to stream
        val videoURL = post.videoUrl

        // Handler for the video player
        val mainHandler = Handler()

        /* A TrackSelector that selects tracks provided by the MediaSource to be consumed by each of the available Renderers.
	  A TrackSelector is injected when the player is created. */
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)


        // Create the player with previously created TrackSelector
        mPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

        // Load the default controller
        mPlayerView.useController = true
        mPlayerView.requestFocus()

        // Load the SimpleExoPlayerView with the created player
        mPlayerView.player = mPlayer

        // Measures bandwidth during playback. Can be null if not required.
        val defaultBandwidthMeter = DefaultBandwidthMeter()

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory =
            DefaultDataSourceFactory(context, Util.getUserAgent(context, "NJD"), defaultBandwidthMeter)


        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory()

        // This is the MediaSource representing the media to be played.
        val videoSource = HlsMediaSource(
            Uri.parse(videoURL),
            dataSourceFactory,
            null, null
        )


        // Prepare the player with the source.
        mPlayer.prepare(videoSource)

        // Autoplay the video when the player is ready
        mPlayer.playWhenReady = true
    }

    private fun getValue() {
        sharedPref = context!!.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userData = sharedPref.getString("store", "")
        val jsonData = JSONObject(userData)
        val jsonResult = jsonData.getJSONObject("result")
        val userName = jsonResult.getString("userName")
        val email = jsonResult.getString("email")

//        val enabled = jsonResult.getString("enabled")

        println("name $userName")
        println("email $email")

//        println("enabled $enabled")
        setData(userName)

    }

    //method to set values in view that fetched from shared preference
    private fun setData(userName: String?) {
        user.text = userName

    }


    override fun onDestroy() {
        super.onDestroy()
        mPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        mPlayer.release()
    }

}


