package com.dovi.bylenilestari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dovi.bylenilestari.ApiServices.ApiClient
import com.dovi.bylenilestari.Request.RequestBodyLogin
import com.dovi.bylenilestari.Response.ResponseLogin
import com.importa.ami.Remote.SharedPreferenceLogin
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private lateinit var checkPassword : CheckBox
    private lateinit var et_password_login : EditText
    private lateinit var et_email_login : EditText
    private lateinit var btn_login : Button
    private lateinit var Loading : ProgressBar
    private lateinit var LupaSandi : TextView
//    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var buatAkun : TextView
    var dialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
        checkPassword = findViewById(R.id.chck_password_login)
        et_password_login = findViewById(R.id.password)
        et_email_login = findViewById(R.id.email)
        btn_login = findViewById(R.id.btnLogin)
        Loading = findViewById(R.id.loading)
        Loading.visibility = View.GONE
        buatAkun = findViewById(R.id.tv_buatAkun)
        LupaSandi = findViewById(R.id.tv_lupa_sandi)

        checkPassword.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                et_password_login.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                et_password_login.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        btn_login.setOnClickListener {
            if (et_email_login.text.isEmpty()){
                et_email_login.error=("Harap isi bidang ini")
                et_email_login.requestFocus()
                return@setOnClickListener
            }
            if (et_password_login.text.isEmpty()){
                et_password_login.error=("Harap isi bidang ini")
                et_password_login.requestFocus()
                return@setOnClickListener
            }
            else{
                Loading.visibility = View.VISIBLE
                login()
            }
        }

        buatAkun.setOnClickListener {
            startActivity(Intent(this@Login, Register::class.java))
            finish()
        }

        LupaSandi.setOnClickListener {
            startActivity(Intent(this@Login, LupaKataSandi::class.java))
            finish()
        }

//        swipeRefreshLayout.setOnRefreshListener {
//            dialogShown = false
//            login()
//            swipeRefreshLayout.isRefreshing = false
//        }
    }

    private fun login() {
        Loading.visibility = View.VISIBLE
        val inputEmail = et_email_login.text.toString()
        val inputPassword = et_password_login.text.toString()
        val requestBody = RequestBodyLogin(email = inputEmail, password = inputPassword)

        ApiClient.create().APiLogin(requestBody).enqueue(object :
            Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        // Simpan alamat email ke SharedPreference
                        responseAPI.data?.email?.let { email ->
                            SharedPreferenceLogin.saveAccountInfo(this@Login, email)
                            Log.d("email","$email")
                        }
                        if (response.code() == 200) {
                            startActivity(Intent(this@Login, BottomNav::class.java))
                            finish()
                            Loading.visibility = View.GONE
                        }
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    try{
                        val errorJson = JSONObject(errorMsg)
                        val message = errorJson.optString("message", "")
                        val status = errorJson.optString("status", "")

                        if (status == "400"){
                            val alertDialogBuilder = AlertDialog.Builder(this@Login)
                            alertDialogBuilder.setTitle("Periksa kembali")
                            alertDialogBuilder.setMessage("$message")
                            alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            val alertDialog = alertDialogBuilder.create()
                            alertDialog.show()
                        }else{

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Loading.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                Loading.visibility = View.GONE
                Log.e("Error", t.message, t)
                Toast.makeText(this@Login,"Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showPopUpDialog(title:String, message: String) {
        val alertDialogBuilder = android.app.AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}