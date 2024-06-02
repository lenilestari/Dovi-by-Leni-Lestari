package com.dovi.bylenilestari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dovi.bylenilestari.ApiServices.ApiClient
import com.dovi.bylenilestari.Request.RequestBodyLogin
import com.dovi.bylenilestari.Request.RequestCekOtp
import com.dovi.bylenilestari.Response.ResponseCekOtp
import com.dovi.bylenilestari.Response.ResponseLogin
import com.importa.ami.Remote.SharedPreferenceLogin
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTP : AppCompatActivity() {
    private lateinit var Loading : ProgressBar
    private lateinit var et_otp : EditText
    private lateinit var btnOtp : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        Loading = findViewById(R.id.loading)
        Loading.visibility = View.GONE
        et_otp = findViewById(R.id.et_emailOtp)
        btnOtp = findViewById(R.id.btnOtp)

        btnOtp.setOnClickListener {
            if (et_otp.text.isEmpty()){
                et_otp.error=("Harap isi bidang ini")
                et_otp.requestFocus()
                return@setOnClickListener
            }
            else{
                Loading.visibility = View.VISIBLE
                verifikasiOtp()
            }
        }
    }

    private fun verifikasiOtp() {
        Loading.visibility = View.VISIBLE
        val inputOtp = et_otp.text.toString().toIntOrNull() ?: 0
        val savedEmail = SharedPreferenceLogin.getEmail(this@OTP)
        val requestBody = RequestCekOtp(otp = inputOtp, email = savedEmail)

        ApiClient.create().ApiCekOtp(requestBody).enqueue(object :
            Callback<ResponseCekOtp> {
            override fun onResponse(call: Call<ResponseCekOtp>, response: Response<ResponseCekOtp>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        if (response.code() == 200) {
                            startActivity(Intent(this@OTP, Login::class.java))
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
                            val alertDialogBuilder = AlertDialog.Builder(this@OTP)
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

            override fun onFailure(call: Call<ResponseCekOtp>, t: Throwable) {
                Loading.visibility = View.GONE
                Log.e("Error", t.message, t)
                Toast.makeText(this@OTP,"Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
            }

        })
    }
}