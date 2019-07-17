package com.underscoretec.njd.Activities

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.SensorManager
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.media.ThumbnailUtils
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.underscoretec.njd.R
import com.underscoretec.njd.Utility.RunTimePermission
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference

class CameraActivity : AppCompatActivity(), SurfaceHolder.Callback, View.OnClickListener {

    internal var flag = 0
    internal var MAX_VIDEO_SIZE_UPLOAD = 25 //MB
    internal var flashType = 1
    internal var myOrientationEventListener: OrientationEventListener? = null
    internal var iOrientation = 0
    internal var mOrientation = 90
    private var surfaceHolder: SurfaceHolder? = null
    private var camera: Camera? = null
    private val customHandler = Handler()
    private var tempFile: File? = null
    private var jpegCallback: Camera.PictureCallback? = null
    private var folder: File? = null
    private var savePicTask: SavePicTask? = null
    private var mPhotoAngle = 90
    private var mediaRecorder: MediaRecorder? = null
    private var imgSurface: SurfaceView? = null
    private var imgCapture: ImageView? = null
    private var imgFlashOnOff: ImageView? = null
    private var imgSwipeCamera: ImageView? = null
    private var runTimePermission: RunTimePermission? = null
    private var textCounter: TextView? = null
    private var hintTextView: TextView? = null
    private var timeInMilliseconds = 0L
    private var startTime = SystemClock.uptimeMillis()
    private var updatedTime = 0L
    private val timeSwapBuff = 0L
    private val updateTimerThread = object : Runnable {

        override fun run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds

            var secs = (updatedTime / 1000).toInt()
            val mins = secs / 60
            val hrs = mins / 60

            secs = secs % 60
            textCounter!!.text = String.format("%02d", mins) + ":" + String.format("%02d", secs)
            customHandler.postDelayed(this, 0)

        }

    }
    private var saveVideoTask: SaveVideoTask? = null
    private var mediaFileName: String? = null

    private val freeSpacePercantage: Int
        get() {
            val percantage = (freeMemory() * 100 / totalMemory()).toInt()
            val modValue = percantage % 5
            return percantage - modValue
        }

    val isSpaceAvailable: Boolean
        get() = freeSpacePercantage >= 1

    private val photoRotation: Int
        get() {
            val rotation: Int
            val orientation = mPhotoAngle

            val info = Camera.CameraInfo()
            if (flag == 0) {
                Camera.getCameraInfo(0, info)
            } else {
                Camera.getCameraInfo(1, info)
            }

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (info.orientation - orientation + 360) % 360
            } else {
                rotation = (info.orientation + orientation) % 360
            }
            return rotation
        }

    override fun onResume() {
        super.onResume()
        try {
            if (myOrientationEventListener != null)
                myOrientationEventListener!!.enable()
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        /* imgSurface!!.isFocusableInTouchMode = true
         imgSurface!!.isFocusable = true
         imgSurface!!.requestFocus()*/

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (runTimePermission != null) {
            runTimePermission!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        supportActionBar!!.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        runTimePermission = RunTimePermission(this)
        runTimePermission!!.requestPermission(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), object : RunTimePermission.RunTimePermissionListener {

                override fun permissionGranted() {
                    // First we need to check availability of play services
                    initControls()

                    identifyOrientationEvents()

                    //create a folder to get image
                    folder = File(Environment.getExternalStorageDirectory().toString() + "/NJD")
                    if (!folder!!.exists()) {
                        folder!!.mkdirs()
                    }
                    //capture image on callback
                    captureImageCallback()
                    if (camera != null) {
                        val info = Camera.CameraInfo()
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            imgFlashOnOff!!.visibility = View.GONE
                        }
                    }
                }

                override fun permissionDenied() {}
            })


    }

    private fun cancelSavePicTaskIfNeed() {
        if (savePicTask != null && savePicTask!!.status == AsyncTask.Status.RUNNING) {
            savePicTask!!.cancel(true)
        }
    }

    private fun cancelSaveVideoTaskIfNeed() {
        if (saveVideoTask != null && saveVideoTask!!.status == AsyncTask.Status.RUNNING) {
            saveVideoTask!!.cancel(true)
        }
    }

    @Throws(IOException::class)
    fun saveToSDCard(data: ByteArray, rotation: Int): String {
        var imagePath = ""
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, 0, data.size, options)

            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)

            val reqHeight = metrics.heightPixels
            val reqWidth = metrics.widthPixels

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, options)



            if (rotation != 0) {
                val mat = Matrix()
                mat.postRotate(rotation.toFloat())
                val bitmap1 = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, mat, true)
                if (bitmap != bitmap1) {
                    bitmap.recycle()
                }
                imagePath = getSavePhotoLocal(bitmap1)
                bitmap1?.recycle()
            } else {
                imagePath = getSavePhotoLocal(bitmap)
                bitmap?.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imagePath
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round(height.toFloat() / reqHeight.toFloat())
            } else {
                inSampleSize = Math.round(width.toFloat() / reqWidth.toFloat())
            }
        }
        return inSampleSize
    }

    private fun getSavePhotoLocal(bitmap: Bitmap?): String {
        var path = ""
        try {
            val output: OutputStream
            val file = File(folder!!.absolutePath, "njd" + System.currentTimeMillis() + ".jpg")
            try {


                output = FileOutputStream(file)
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, output)
                output.flush()
                output.close()
                path = file.absolutePath
                println("get absolute path >>>>>>>>>>>>>>>>>>>>>>$path")
//                upload.uploadToServer(path)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return path
    }

    private fun captureImageCallback() {

        surfaceHolder = imgSurface!!.holder
        surfaceHolder!!.addCallback(this)
        surfaceHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        jpegCallback = Camera.PictureCallback { data, camera ->
            refreshCamera()

            cancelSavePicTaskIfNeed()
            savePicTask = SavePicTask(data, photoRotation)
            savePicTask!!.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)
        }
    }

    private fun identifyOrientationEvents() {

        myOrientationEventListener = object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(iAngle: Int) {

                val iLookup = intArrayOf(
                    0,
                    0,
                    0,
                    90,
                    90,
                    90,
                    90,
                    90,
                    90,
                    180,
                    180,
                    180,
                    180,
                    180,
                    180,
                    270,
                    270,
                    270,
                    270,
                    270,
                    270,
                    0,
                    0,
                    0
                ) // 15-degree increments
                if (iAngle != OrientationEventListener.ORIENTATION_UNKNOWN) {

                    val iNewOrientation = iLookup[iAngle / 15]
                    if (iOrientation != iNewOrientation) {
                        iOrientation = iNewOrientation
                        if (iOrientation == 0) {
                            mOrientation = 90
                        } else if (iOrientation == 270) {
                            mOrientation = 0
                        } else if (iOrientation == 90) {
                            mOrientation = 180
                        }

                    }
                    mPhotoAngle = normalize(iAngle)
                }
            }
        }

        if (myOrientationEventListener!!.canDetectOrientation()) {
            myOrientationEventListener!!.enable()
        }

    }

    private fun initControls() {

        mediaRecorder = MediaRecorder()

        imgSurface = findViewById<View>(R.id.imgSurface) as SurfaceView
        textCounter = findViewById<View>(R.id.textCounter) as TextView
        imgCapture = findViewById<View>(R.id.imgCapture) as ImageView
        imgFlashOnOff = findViewById<View>(R.id.imgFlashOnOff) as ImageView
        imgSwipeCamera = findViewById<View>(R.id.imgChangeCamera) as ImageView
        textCounter!!.visibility = View.GONE

        hintTextView = findViewById<View>(R.id.hintTextView) as TextView

        imgSwipeCamera!!.setOnClickListener(this)
        activeCameraCapture()

        imgFlashOnOff!!.setOnClickListener(this)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        cancelSavePicTaskIfNeed()
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgFlashOnOff -> flashToggle()
            R.id.imgChangeCamera -> {
                camera!!.stopPreview()
                camera!!.release()
                if (flag == 0) {
                    imgFlashOnOff!!.visibility = View.GONE
                    flag = 1
                } else {
                    imgFlashOnOff!!.visibility = View.VISIBLE
                    flag = 0
                }
                surfaceCreated(surfaceHolder)
            }
            else -> {
            }
        }
    }

    //------------------SURFACE CREATED FIRST TIME--------------------//

    private fun flashToggle() {

        if (flashType == 1) {

            flashType = 2
        } else if (flashType == 2) {

            flashType = 3
        } else if (flashType == 3) {

            flashType = 1
        }
        refreshCamera()
    }

//    private fun captureImage() {
//        camera!!.takePicture(null, null, jpegCallback)
//        inActiveCameraCapture()
//    }

    private fun releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder!!.reset()   // clear recorder configuration
            mediaRecorder!!.release() // release the recorder object
            mediaRecorder = MediaRecorder()
        }
    }

    fun refreshCamera() {

        if (surfaceHolder!!.surface == null) {
            return
        }
        try {
            camera!!.stopPreview()
            val param = camera!!.parameters

            if (flag == 0) {
                if (flashType == 1) {
                    param.flashMode = Camera.Parameters.FLASH_MODE_AUTO
                    param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    imgFlashOnOff!!.setImageResource(R.drawable.flash_auto)
                } else if (flashType == 2) {
                    param.flashMode = Camera.Parameters.FLASH_MODE_ON
                    param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    var params: Camera.Parameters? = null
                    if (camera != null) {
                        params = camera!!.parameters

                        if (params != null) {
                            val supportedFlashModes = params.supportedFlashModes

                            if (supportedFlashModes != null) {
                                if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                                    param.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                                    param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                                } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                                    param.flashMode = Camera.Parameters.FLASH_MODE_ON
                                    param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                                }
                            }
                        }
                    }
                    imgFlashOnOff!!.setImageResource(R.drawable.flash_on)
                } else if (flashType == 3) {
                    param.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    imgFlashOnOff!!.setImageResource(R.drawable.flash_off)
                }
            }


            refrechCameraPriview(param)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //------------------SURFACE OVERRIDE METHODS END--------------------//

    private fun refrechCameraPriview(param: Camera.Parameters) {
        try {
            camera!!.parameters = param
            setCameraDisplayOrientation(0)

            camera!!.setPreviewDisplay(surfaceHolder)
            camera!!.startPreview()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setCameraDisplayOrientation(cameraId: Int) {

        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)

        var rotation = windowManager.defaultDisplay.rotation

        if (Build.MODEL.equals("Nexus 6", ignoreCase = true) && flag == 1) {
            rotation = Surface.ROTATION_180
        }
        var degrees = 0
        when (rotation) {

            Surface.ROTATION_0 ->

                degrees = 0

            Surface.ROTATION_90 ->

                degrees = 90

            Surface.ROTATION_180 ->

                degrees = 180

            Surface.ROTATION_270 ->

                degrees = 270
        }

        var result: Int

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror

        } else {
            result = (info.orientation - degrees + 360) % 360

        }

        camera!!.setDisplayOrientation(result)

    }

    override fun surfaceCreated(arg0: SurfaceHolder?) {
        try {
            if (flag == 0) {
                camera = Camera.open(0)
            } else {
                camera = Camera.open(1)
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
            return
        }

        try {
            val param: Camera.Parameters = camera!!.parameters
            for (size in param.supportedPictureSizes) {

                if ((1600 <= size.width) and (size.width <= 1920)) {
                    param.setPreviewSize(size.width, size.height)
                    param.setPictureSize(size.width, size.height)
                    break
                }
            }
            /*    param.setPictureSize(camera.width, cs.height);*/
            param.zoom = 0
            param.jpegQuality = 100
            camera!!.parameters = param
            setCameraDisplayOrientation(0)
            camera!!.setPreviewDisplay(surfaceHolder)
            camera!!.startPreview()

            if (flashType == 1) {

                param.flashMode = Camera.Parameters.FLASH_MODE_AUTO
                param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                param.videoStabilization = true
                imgFlashOnOff!!.setImageResource(R.drawable.flash_auto)

            } else if (flashType == 2) {
                param.flashMode = Camera.Parameters.FLASH_MODE_ON
                param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                param.videoStabilization = true
                var params: Camera.Parameters? = null
                if (camera != null) {
                    params = camera!!.parameters

                    if (params != null) {
                        val supportedFlashModes = params.supportedFlashModes

                        if (supportedFlashModes != null) {
                            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                                param.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                                param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                                param.videoStabilization = true
                            } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                                param.flashMode = Camera.Parameters.FLASH_MODE_ON
                                param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

                                param.videoStabilization = true
                            }
                        }
                    }
                }
                imgFlashOnOff!!.setImageResource(R.drawable.flash_on)

            } else if (flashType == 3) {
                param.flashMode = Camera.Parameters.FLASH_MODE_OFF
                param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                param.videoStabilization = true
                imgFlashOnOff!!.setImageResource(R.drawable.flash_off)
            }


        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

    }

    override fun surfaceDestroyed(arg0: SurfaceHolder) {
        try {
            camera!!.stopPreview()
            camera!!.release()
            camera = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        refreshCamera()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun scaleUpAnimation() {
        val scaleDownX = ObjectAnimator.ofFloat(imgCapture, "scaleX", 2f)
        val scaleDownY = ObjectAnimator.ofFloat(imgCapture, "scaleY", 2f)
        scaleDownX.duration = 100
        scaleDownY.duration = 100
        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)

        scaleDownX.addUpdateListener {
            val p = imgCapture!!.parent as View
            p.invalidate()
        }
        scaleDown.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun scaleDownAnimation() {
        val scaleDownX = ObjectAnimator.ofFloat(imgCapture, "scaleX", 1f)
        val scaleDownY = ObjectAnimator.ofFloat(imgCapture, "scaleY", 1f)
        scaleDownX.duration = 100
        scaleDownY.duration = 100
        val scaleDown = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)

        scaleDownX.addUpdateListener {
            val p = imgCapture!!.parent as View
            p.invalidate()
        }
        scaleDown.start()
    }

    override fun onPause() {
        super.onPause()

        try {

            customHandler.removeCallbacksAndMessages(null)

            releaseMediaRecorder()
            // if you are using MediaRecorder, release it first

            if (myOrientationEventListener != null)
                myOrientationEventListener!!.enable()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun activeCameraCapture() {
        if (imgCapture != null) {

//            imgCapture!!.alpha = 1.0f
//            imgCapture!!.setOnClickListener(View.OnClickListener {
//                hintTextView!!.visibility = View.INVISIBLE
//                try {
//                    if (prepareMediaRecorder()) {
//                        myOrientationEventListener!!.disable()
//                        mediaRecorder!!.start()
//                        startTime = SystemClock.uptimeMillis()
//                        customHandler.postDelayed(updateTimerThread, 0)
//                    } else {
//                        return@OnClickListener
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//                textCounter!!.visibility = View.VISIBLE
//                imgSwipeCamera!!.visibility = View.GONE
//                imgFlashOnOff!!.visibility = View.GONE
//                scaleUpAnimation()
//                imgCapture!!.setOnTouchListener(View.OnTouchListener { v, event ->
//                    if (event.action == MotionEvent.ACTION_BUTTON_PRESS) {
//                        return@OnTouchListener true
//                    }
//                    if (event.action == MotionEvent.ACTION_UP) {
//
//                        scaleDownAnimation()
//                        hintTextView!!.visibility = View.VISIBLE
//
//                        cancelSaveVideoTaskIfNeed()
//                        saveVideoTask = SaveVideoTask(this)
//
//                        println("save video task>>>>>>>>>>>>>$saveVideoTask")
//                        saveVideoTask!!.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)
//
//                        return@OnTouchListener true
//                    }
//                    true
//                })
//
//            })
            imgCapture!!.setOnClickListener {
                if (isSpaceAvailable) {
                    //captureImage()
                    hintTextView!!.visibility = View.INVISIBLE
                    try {
                        if (prepareMediaRecorder()) {
                            myOrientationEventListener!!.disable()
                            mediaRecorder!!.start()
                            startTime = SystemClock.uptimeMillis()
                            customHandler.postDelayed(updateTimerThread, 0)
                        } else {
                            return@setOnClickListener
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    textCounter!!.visibility = View.VISIBLE
                    imgSwipeCamera!!.visibility = View.GONE
                    imgFlashOnOff!!.visibility = View.GONE
                    // scaleUpAnimation()

                    imgCapture!!.setOnTouchListener(View.OnTouchListener { v, event ->

                        if (event.action == MotionEvent.ACTION_UP) {

                            //scaleDownAnimation()
                            hintTextView!!.visibility = View.VISIBLE

                            cancelSaveVideoTaskIfNeed()
                            saveVideoTask = SaveVideoTask(this)

                            println("save video task>>>>>>>>>>>>>$saveVideoTask")
                            saveVideoTask!!.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)

                            return@OnTouchListener true
                        }
                        true
                    })


                } else {
                    Toast.makeText(this@CameraActivity, "Memory is not available", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    //--------------------------CHECK FOR MEMORY -----------------------------//

    @SuppressLint("StringFormatMatches")
    fun onVideoSendDialog(videopath: String?, thumbPath: String) {

        runOnUiThread {
            if (videopath != null) {
                val fileVideo = File(videopath)
                val fileSizeInBytes = fileVideo.length()
                val fileSizeInKB = fileSizeInBytes / 1024
                val fileSizeInMB = fileSizeInKB / 102400
                if (fileSizeInMB > MAX_VIDEO_SIZE_UPLOAD) {
                    android.support.v7.app.AlertDialog.Builder(this@CameraActivity)
                        .setMessage(getString(R.string.file_limit_size_upload_format, MAX_VIDEO_SIZE_UPLOAD))
                        .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                        .show()
                } else {

                    var mIntent = Intent(this@CameraActivity, Activity_create_post::class.java)
                    mIntent.putExtra("PATH", videopath.toString())
                    mIntent.putExtra("THUMB", thumbPath)
                    mIntent.putExtra("WHO", "Video")
                    startActivity(mIntent)
                    finish()

                    //SendVideoDialog sendVideoDialog = SendVideoDialog.newInstance(videopath, thumbPath, name, phoneNuber);
                    // sendVideoDialog.show(getSupportFragmentManager(), "SendVideoDialog");
                }
            }
        }
    }

    private fun inActiveCameraCapture() {
        if (imgCapture != null) {
            imgCapture!!.alpha = 0.5f
            imgCapture!!.setOnClickListener(null)
        }
    }

    fun totalMemory(): Double {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val sdAvailSize = stat.blockCount.toDouble() * stat.blockSize.toDouble()
        return sdAvailSize / 1073741824
    }
    //-------------------END METHODS OF CHECK MEMORY--------------------------//

    fun freeMemory(): Double {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val sdAvailSize = stat.availableBlocks.toDouble() * stat.blockSize.toDouble()
        return sdAvailSize / 1073741824
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    protected fun prepareMediaRecorder(): Boolean {

        mediaRecorder = MediaRecorder() // Works well
        camera!!.stopPreview()
        camera!!.unlock()
        mediaRecorder!!.setCamera(camera)
        mediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        if (flag == 1) {

            mediaRecorder!!.setProfile(CamcorderProfile.get(1, CamcorderProfile.QUALITY_HIGH))
        } else {
            mediaRecorder!!.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
        }
        mediaRecorder!!.setPreviewDisplay(surfaceHolder!!.surface)

        mediaRecorder!!.setOrientationHint(mOrientation)

        if (Build.MODEL.equals("Nexus 6", ignoreCase = true) && flag == 1) {

            if (mOrientation == 90) {
                mediaRecorder!!.setOrientationHint(mOrientation)
            } else if (mOrientation == 180) {
                mediaRecorder!!.setOrientationHint(0)
            } else {
                mediaRecorder!!.setOrientationHint(180)
            }

        } else if (mOrientation == 90 && flag == 1) {
            mediaRecorder!!.setOrientationHint(270)
        } else if (flag == 1) {
            mediaRecorder!!.setOrientationHint(mOrientation)
        }
        mediaFileName = "njd_vid_" + System.currentTimeMillis()
        mediaRecorder!!.setOutputFile(folder!!.absolutePath + "/" + mediaFileName + ".mp4") // Environment.getExternalStorageDirectory()

        mediaRecorder!!.setOnInfoListener { mr, what, extra ->
            // TODO Auto-generated method stub

            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {

                val downTime: Long = 0
                val eventTime: Long = 0
                val x = 0.0f
                val y = 0.0f
                val metaState = 0
                val motionEvent = MotionEvent.obtain(
                    downTime,
                    eventTime,
                    MotionEvent.ACTION_UP,
                    0f,
                    0f,
                    metaState
                )

                imgCapture!!.dispatchTouchEvent(motionEvent)

                Toast.makeText(this@CameraActivity, "You reached to Maximum(25MB) video size.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        mediaRecorder!!.setMaxFileSize((1000 * 1024 * 1000).toLong())

        try {
            mediaRecorder!!.prepare()
        } catch (e: Exception) {
            releaseMediaRecorder()
            e.printStackTrace()
            return false
        }

        return true

    }

    fun generateVideoThmb(srcFilePath: String, destFile: File) {
        try {
            val bitmap = ThumbnailUtils.createVideoThumbnail(srcFilePath, 120)
            val out = FileOutputStream(destFile)
            ThumbnailUtils.extractThumbnail(bitmap, 1080, 1920).compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun normalize(degrees: Int): Int {
        if (degrees > 315 || degrees <= 45) {
            return 0
        }

        if (degrees > 45 && degrees <= 135) {
            return 90
        }

        if (degrees > 135 && degrees <= 225) {
            return 180
        }

        if (degrees > 225 && degrees <= 315) {
            return 270
        }

        throw RuntimeException("Error....")
    }

    private inner class SavePicTask(private val data: ByteArray, rotation: Int) : AsyncTask<Void, Void, String>() {
        private var rotation = 0
        internal var progressDialog1: ProgressDialog? = null

        init {
            this.rotation = rotation
        }

        override fun onPreExecute() {

            progressDialog1 = ProgressDialog(this@CameraActivity)
            progressDialog1!!.setMessage("Processing a image...")
            progressDialog1!!.show()

        }

        override fun doInBackground(vararg params: Void): String? {

            try {
                return saveToSDCard(data, rotation)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(result: String) {

            activeCameraCapture()

            tempFile = File(result)

            if (progressDialog1 != null) {
                if (progressDialog1!!.isShowing) {
                    progressDialog1!!.dismiss()
                }
            }

            Handler().postDelayed({

                /*function call to upload image pick by camera to server*/
//                var upload = Upload(profile)
//                upload.uploadToServer(tempFile!!.absolutePath)
                var mIntent = Intent(this@CameraActivity, Activity_create_post::class.java)
                mIntent.putExtra("PATH", tempFile.toString())
//                mIntent.putExtra("THUMB", tempFile.toString())
//                mIntent.putExtra("WHO", "Image")
                startActivity(mIntent)
            }, 100)


        }
    }

    private class SaveVideoTask internal constructor(context: CameraActivity) : AsyncTask<Void, Void, String>() {

        private val activityReference: WeakReference<CameraActivity> = WeakReference(context)
        private var thumbFilename: File? = null
        private var progressDialog: ProgressDialog? = null


        override fun onPreExecute() {
            super.onPreExecute()

            progressDialog = ProgressDialog(activityReference.get())
            progressDialog!!.setMessage("Processing a video...")
            progressDialog!!.show()
            activityReference.get()!!.imgCapture!!.setOnTouchListener(null)
            activityReference.get()!!.textCounter!!.visibility = View.GONE
            activityReference.get()!!.imgSwipeCamera!!.visibility = View.VISIBLE
            activityReference.get()!!.imgFlashOnOff!!.visibility = View.VISIBLE

        }


        override fun doInBackground(vararg params: Void?): String? {
            //To change body of created functions use File | Settings | File Templates.

            try {
                try {

                    activityReference.get()!!.myOrientationEventListener!!.enable()

                    activityReference.get()!!.customHandler.removeCallbacksAndMessages(null)

                    activityReference.get()!!.mediaRecorder!!.stop()
                    activityReference.get()!!.releaseMediaRecorder()

                    activityReference.get()!!.tempFile =
                        File(activityReference.get()!!.folder!!.absolutePath + "/" + activityReference.get()!!.mediaFileName + ".mp4")
                    thumbFilename = File(
                        activityReference.get()!!.folder!!.absolutePath,
                        "t_$activityReference.get()!!.mediaFileName.jpeg"
                    )
                    activityReference.get()!!.generateVideoThmb(
                        activityReference.get()!!.tempFile!!.path,
                        thumbFilename!!
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                e.printStackTrace()

            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (progressDialog != null) {
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }

            if (activityReference.get()!!.tempFile != null && thumbFilename != null)
                activityReference.get()!!.onVideoSendDialog(
                    activityReference.get()!!.tempFile!!.absolutePath,
                    thumbFilename!!.absolutePath
                )
        }
    }

}
