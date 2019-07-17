package com.underscoretec.njd.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.make
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.underscoretec.njd.Helpers.MySingleton
import com.underscoretec.njd.R
import com.underscoretec.njd.Utility.SDUtility
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class RegistrationActivity : AppCompatActivity() {

    var requestQueue: RequestQueue? = null
    val TAG = "MyActivity"
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        requestQueue = MySingleton.getInstance(this.applicationContext).requestQueue
        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        onTapUi()
    }

    fun onTapUi() {

        tvlogin.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }

        rigsign.setOnClickListener {
            if (rietuser.text.toString().trim().isNotEmpty() && ritemail.text.toString().trim().isNotEmpty()) {
                val jsonObject = JSONObject()
                jsonObject.put("userName", rietuser.text.toString().trim())
                jsonObject.put("email", ritemail.text.toString().trim())
                doRegistration(jsonObject)

            } else {
                if (ritemail.text.toString().isEmpty() && rietuser.text.toString().isEmpty()){
                    rietuser.error = "Please enter your Username"
                    ritemail.error = "Please enter your Email"
                }
                else if (ritemail.text.toString().isEmpty()) {
                    ritemail.error = "Please enter your Email"
                } else {
                    rietuser.error = "Please enter your Username"
                }

            }

        }

    }

    private fun doRegistration(data: JSONObject) {
        if (SDUtility.isNetworkAvailable(this@RegistrationActivity)) {
            println("IN jsonPARSE")
            showProgressBar(my_progressbar)
            disableView()

            val url = "https://njd.api.underscoretec.com/user"
            val jsonObjReq =
                object : JsonObjectRequest(Method.POST, url, data, Response.Listener { response ->
                    hideProgressBar(my_progressbar)
                    enableView()
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
                                editor.putBoolean("loginStatus", false)
                                editor.apply()
                                finish()
                            }
                            //chceking activity_login status

                        }
                    } catch (e: JSONException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                        SDUtility.displayExceptionMessage(e.message, this)
                    }
                },
                    Response.ErrorListener { error ->
                        hideProgressBar(my_progressbar)
                        enableView()
                        VolleyLog.e(TAG, "Error" + error.message)

                    }) {

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        return headers
                    }
                }

            MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
        } else {
            enableView()
            val mysnackbar = make(
                registration_coordinate,
                "You are not connected to the Internet,Please check your Internet Connection",
                Snackbar.LENGTH_LONG
            )
            mysnackbar.setAction("Retry", View.OnClickListener {
                doRegistration(data)
            })
            mysnackbar.show()
        }
    }

    fun hideProgressBar(myProgressbar: ProgressBar) {
        myProgressbar.visibility = View.GONE
    }

    fun showProgressBar(myProgressbar: ProgressBar) {
        myProgressbar.visibility = View.VISIBLE
    }

    fun disableView() {
        rigsign.isEnabled = false
    }

    fun enableView() {
        rigsign.isEnabled = true
    }
}