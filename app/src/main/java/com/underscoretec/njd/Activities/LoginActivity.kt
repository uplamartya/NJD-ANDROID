package com.underscoretec.njd.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.underscoretec.njd.Helpers.MySingleton
import com.underscoretec.njd.R
import com.underscoretec.njd.Utility.SDUtility
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {


    lateinit var click_registration: TextView
    internal var mQueue: RequestQueue? = null
    lateinit var sharedPref: SharedPreferences
    val TAG = "MyActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        mQueue = MySingleton.getInstance(this.applicationContext).requestQueue
        initUielement()
        onTapUi()

    }

    fun initUielement() {
        click_registration = findViewById(R.id.tvclick)
    }

    fun onTapUi() {

        click_registration.setOnClickListener {

            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        sing_in_btn.setOnClickListener {
            if (etemail.text.toString().trim().isNotEmpty() && etpass.text.toString().trim().isNotEmpty()) {

                val data = JSONObject()
                data.put("email", etemail.text.toString().trim())
                data.put("password", etpass.text.toString().trim())
                dosingin(data)
            } else {
                if (etemail.text.toString().trim().isEmpty() && etpass.text.toString().isEmpty()) {
                    etemail.error = "Please enter your Email"
                    etpass.error = "Please enter your Password"
                } else if (etemail.text.toString().isEmpty()) {
                    etemail.error = "Please enter your Email"
                } else {
                    etpass.error = "Please enter your Password"
                }
            }
        }

    }

    private fun dosingin(data: JSONObject) {

        if (SDUtility.isNetworkAvailable(this@LoginActivity)) {
            showProgressBar(my_login_progressbar)
            disableView()

            val url = "https://njd.api.underscoretec.com/user/login"
            val jsonObjReq = object : JsonObjectRequest(
                Method.POST, url, data, Response.Listener { response ->
                    hideProgressBar(my_login_progressbar)
                    enableView()
                    Log.v(TAG, response.toString())

                    //TODO store in sharedPreference

                    try {
                        val serverResp = JSONObject(response.toString())
                        println("success result: $serverResp")
                        SDUtility.displayExceptionMessage(serverResp.getString("message"), applicationContext)
                        val errorStatus = serverResp.getString("error")
                        if (errorStatus == "true") {
                            val errorMessage = serverResp.getString("message")
                            SDUtility.displayExceptionMessage(errorMessage, applicationContext)
                        } else {
                            run {
                                //storing response in shared preference
                                val editor = sharedPref.edit()
                                editor.putString("store", response.toString())
                                editor.putBoolean("loginStatus", true)
                                editor.apply()
                                val intent = Intent(
                                    this@LoginActivity,
                                    DashboardActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }
                    } catch (e: JSONException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                        SDUtility.displayExceptionMessage(e.message, this)
                    }
                },
                Response.ErrorListener { error ->
                    hideProgressBar(my_login_progressbar)
                    enableView()
                    VolleyLog.e(TAG, "Error" + error.message)
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

            enableView()
            val mysnackbar = Snackbar.make(
                my_coordinator,
                "You are not connected to the Internet,Please check your Internet Connection",
                Snackbar.LENGTH_LONG
            )
            mysnackbar.setAction("Retry", View.OnClickListener {
                dosingin(data)
            })
            mysnackbar.show()
        }

    }

    fun showProgressBar(myProgressbar: ProgressBar) {
        myProgressbar.visibility = View.VISIBLE
    }

    fun hideProgressBar(progress: ProgressBar) {
        progress.visibility = View.GONE
    }

    fun disableView() {
        sing_in_btn.isEnabled = false
    }

    fun enableView() {
        sing_in_btn.isEnabled = true
    }

}
