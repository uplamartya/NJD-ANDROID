package com.underscoretec.njd.Activities

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.JsonElement
import com.underscoretec.njd.Helpers.MySingleton
import com.underscoretec.njd.R
import com.underscoretec.njd.Utility.SDUtility
import com.underscoretec.njd.VideoInterface
import kotlinx.android.synthetic.main.activity_camera.imgCapture
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class Activity_create_post : AppCompatActivity() {


    //    var context: Context? = null
    var filePath: String = ""
    lateinit var file: File
    lateinit var mPlayerView: SimpleExoPlayerView
    lateinit var mPlayer: SimpleExoPlayer
    lateinit var sharedPref: SharedPreferences
    var jwtToken: String = ""
    var _id: String = ""
    internal var mQueue: RequestQueue? = null
    var videourl: String = ""
    var videothumbnail: String = ""
    val TAG = "CREATEPOSTACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        initUiElements()
        onClickUI()
        getValue()
        getPlayer()


    }

    private fun initUiElements() {
        //get filepath of video from previous intent
        filePath = intent.getStringExtra("PATH")
        mPlayerView = findViewById(R.id.preview_video)

        mQueue = MySingleton.getInstance(this.applicationContext).requestQueue
    }

    fun onClickUI() {

        imgCapture.setOnClickListener {
            finish()
        }
        close_icon.setOnClickListener {
            finish()
        }

        btn_share.setOnClickListener {
            if (filePath != "") {
                if (_id != "" && jwtToken != "") {
                    if (editText_title.text.toString().trim().isNotEmpty()) {
                        uploadVideoToServer(filePath)
                    } else {
                        SDUtility.displayExceptionMessage("Please add a Video Title",this@Activity_create_post)
                    }

                }

            }

        }

    }

    //Set server Timeout

    fun configureTimeouts(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS) // Set your timeout duration here.
            .writeTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    private fun uploadVideoToServer(pathToVideoFile: String) {
        showProgressBar(loader)
        disableView()
        val videoFile = File(pathToVideoFile)
        val videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile)
        val vFile = MultipartBody.Part.createFormData("file", videoFile.name, videoBody)
        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .client(configureTimeouts())
            .build()
        val vInterface = retrofit.create(VideoInterface::class.java)
        val serverCom = vInterface.uploadVideoToServer(vFile)
        serverCom.enqueue(object : Callback<JsonElement> {

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                val res = response.body().toString()
                val resObject = JSONObject(res)
                videourl = resObject.getString("url")
                videothumbnail = resObject.getString("thumbnail")
                println("Activity_create_post.onResponse $res")
                if (videourl != "" && videothumbnail != "") {
                    //method to create post
                    createPost(videourl, videothumbnail)
                }

                hideProgressBar(loader)
//                finish()
//                startActivity(Intent(this@Activity_create_post, DashboardActivity::class.java))
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d("TAG", "Error message " + t.message)
                hideProgressBar(loader)
                enableView()
            }
        })
    }

    //method to craete post with uploaded video url
    private fun createPost(url: String, videothumbnail: String) {
        println("INSIDE CREATE POST")
        val bodyObject = JSONObject()
        bodyObject.put("userId", _id)
        if (editText_title.text.toString().trim().isNotEmpty()) {
            bodyObject.put("text", editText_title.text.toString().trim())
        }
        bodyObject.put("videoUrl", url)
        bodyObject.put("type", "jjj")
        bodyObject.put("category", "Entertainment")
        bodyObject.put("thumbNail", videothumbnail)

        println("Activity_create_post.createPost$bodyObject")

        if (SDUtility.isNetworkAvailable(this@Activity_create_post)) {
            showProgressBar(loader)
            disableView()

            val url = "https://njd.api.underscoretec.com/create/post"
            val jsonObjReq = object : JsonObjectRequest(
                Method.POST, url, bodyObject, com.android.volley.Response.Listener { response ->
                    hideProgressBar(loader)
                    enableView()
                    println("REsponse of create post$response")

                    try {
                        val serverResp = JSONObject(response.toString())
                        println("success result: $serverResp")
                        SDUtility.displayExceptionMessage(serverResp.getString("message"), applicationContext)
                        val errorStatus = serverResp.getString("error")
                        if (errorStatus == "true") {
                            val errorMessage = serverResp.getString("message")
                            SDUtility.displayExceptionMessage(errorMessage, applicationContext)
                        } else {
                            finish()

                        }
                    } catch (e: JSONException) {

                        e.printStackTrace()
                        SDUtility.displayExceptionMessage(e.message, this)
                    }
                },
                com.android.volley.Response.ErrorListener { error ->
                    hideProgressBar(loader)
                    enableView()
                    VolleyLog.e("Error of Create Post", "Error" + error.message)
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["authorization"] = jwtToken
                    return headers
                }
            }
            jsonObjReq.tag = TAG
            mQueue!!.add(jsonObjReq)
        } else {

            enableView()
            val mysnackbar = Snackbar.make(
                my_coordinator, "You are not connected to the Internet,Please check your Internet Connection",
                Snackbar.LENGTH_LONG
            )
            mysnackbar.setAction("Retry", View.OnClickListener {
                if (videourl != "") {
                    //method to create post with uploaded video url
                    createPost(videourl, videothumbnail)
                } else {
                    SDUtility.displayExceptionMessage("Please Upload the Video", this@Activity_create_post)
                }
            })
            mysnackbar.show()
        }


    }

    private fun getPlayer() {


        /* A TrackSelector that selects tracks provided by the MediaSource to be consumed by each of the available Renderers.
	  A TrackSelector is injected when the player is created. */
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)


        // Create the player with previously created TrackSelector
        mPlayer = ExoPlayerFactory.newSimpleInstance(this@Activity_create_post, trackSelector)

        // Load the default controller
        mPlayerView!!.useController = true
        mPlayerView!!.requestFocus()

        // Load the SimpleExoPlayerView with the created player
        mPlayerView!!.player = mPlayer

        // Measures bandwidth during playback. Can be null if not required.

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory =
            DefaultDataSourceFactory(
                this@Activity_create_post,
                Util.getUserAgent(this@Activity_create_post, "NJD")
            )


        // Produces Extractor instances for parsing the media data.

        // This is the MediaSource representing the media to be played.
        val videoSource = ExtractorMediaSource(
            Uri.fromFile(File(filePath)),
            dataSourceFactory, DefaultExtractorsFactory(),
            null, null
        )


        // Prepare the player with the source.
        mPlayer.prepare(videoSource)

        // Autoplay the video when the player is ready
        mPlayer.setPlayWhenReady(true)
    }

    private fun getValue() {
        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userData = sharedPref.getString("store", "")
        val jsonData = JSONObject(userData)
        jwtToken = jsonData.getString("jwt-Token")
        val jsonResult = jsonData.getJSONObject("result")
        val userName = jsonResult.getString("userName")
        _id = jsonResult.getString("_id")


        setData(userName)

    }

    private fun setData(person_name: String?) {
        p_name.text = person_name
    }

    companion object {
        private const val SERVER_PATH = "https://njd.api.underscoretec.com"

    }

    fun hideProgressBar(myProgressbar: ProgressBar) {
        myProgressbar.visibility = View.GONE
    }

    fun showProgressBar(myProgressbar: ProgressBar) {
        myProgressbar.visibility = View.VISIBLE
    }

    fun disableView() {
        imgCapture.isEnabled = false
    }

    fun enableView() {
        imgCapture.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer.release()
    }
}