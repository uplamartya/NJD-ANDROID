package com.underscoretec.njd.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Toast
import com.underscoretec.njd.Fragment.*
import com.underscoretec.njd.Models.Model_post
import com.underscoretec.njd.R


import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initUielement()
        onTapUi()
        relative_home.performClick()
    }

    fun initUielement() {


    }

    fun onTapUi() {

        relative_home.setOnClickListener {
            view_home.visibility = View.VISIBLE
            view_search.visibility = View.GONE
            view_notification.visibility = View.GONE
            view_camera.visibility = View.GONE
            view_user.visibility = View.GONE

            openFragment(R.id.my_frame, Home())
        }

        relative_user.setOnClickListener {
            view_home.visibility = View.GONE
            view_search.visibility = View.GONE
            view_notification.visibility = View.GONE
            view_camera.visibility = View.GONE
            view_user.visibility = View.VISIBLE

            openFragment(
                R.id.my_frame,
                Profile_fragment()
            )

        }

        relative_notification.setOnClickListener {
            view_home.visibility = View.GONE
            view_search.visibility = View.GONE
            view_notification.visibility = View.VISIBLE
            view_camera.visibility = View.GONE
            view_user.visibility = View.GONE
            openFragment(R.id.my_frame, NotificationFragment())
        }

        relative_search.setOnClickListener {
            view_home.visibility = View.GONE
            view_search.visibility = View.VISIBLE
            view_notification.visibility = View.GONE
            view_camera.visibility = View.GONE
            view_user.visibility = View.GONE

           // openFragment(R.id.my_frame, SearchFragment())
        }

        relative_camera.setOnClickListener {

            view_home.visibility = View.GONE
            view_search.visibility = View.GONE
            view_notification.visibility = View.GONE
            view_camera.visibility = View.VISIBLE
            view_user.visibility = View.GONE

            val intent = Intent(this@DashboardActivity, CameraActivity::class.java)
            startActivity(intent)


        }

    }

    //function to launch fragment
     fun openFragment(myFrame: Int, frag: Fragment) {
        val fragmentManager = supportFragmentManager
        overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_top)
        fragmentManager.beginTransaction().replace(myFrame, frag).commit()

    }

    fun openVideoDescFragment(post: Model_post) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.videoDesc_frame, Video_description_fragment(post)).addToBackStack(null).commit()

    }

    //function to close Fragment
    fun closeFragment() {

        val fm = this@DashboardActivity.supportFragmentManager

        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
            for (i in 1..fm.backStackEntryCount) {
                fm.popBackStack()
            }
        }
    }

    private var hideBottomNavigation = false
    private var doubleBackToExitPressedOnce = false
    //implemented double backpress to exit app
    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            //function to show BottomNavigation
           // show_BottomNavigation()
            return
        } else {

                //function to close fragment
                closeFragment()
                //function to show BottomNavigation
                //show_BottomNavigation()
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()


            Handler().postDelayed( { doubleBackToExitPressedOnce = false }, 2000)
        }


    }
}
