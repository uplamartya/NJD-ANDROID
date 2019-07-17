package com.underscoretec.njd.Fragment



import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.underscoretec.njd.Models.Model_post
import com.underscoretec.njd.R
import kotlinx.android.synthetic.main.fragment_video_description_fragment.*
import org.json.JSONObject
import java.util.ArrayList


class Video_description_fragment : Fragment() {
    lateinit var mPlayerView: SimpleExoPlayerView
    lateinit var mPlayer: SimpleExoPlayer
    lateinit var user : TextView
    lateinit var relatedVideo: RecyclerView
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_video_description_fragment, container, false)
        mPlayerView = view.findViewById(R.id.player)
        getPlayer()
        user = view.findViewById(R.id.user_name)
        getValue()

        relatedVideo = view.findViewById(R.id.recycler_related_video)

        relatedVideo.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val user = ArrayList<Model_post>()

        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))
        user.add(Model_post(R.drawable.image_1, R.drawable.userimage, "Now Just Dance All Performance World of Dance 2019....."))


        val adapter = CustomAdapter(user, context)
        relatedVideo.adapter = adapter
        relatedVideo.setItemViewCacheSize(20)
        relatedVideo.setHasFixedSize(true)

        return view
    }

    private fun getPlayer() {
        // URL of the video to stream
        val videoURL = "https://videodelivery.net/1584a1a702d494e3ddf52081d4a0b168/manifest/video.m3u8"

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


