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
import com.dovi.bylenilestari.Request.RequestCekEmail
import com.dovi.bylenilestari.Request.RequestCekOtp
import com.dovi.bylenilestari.Response.ResponseCekEmail
import com.dovi.bylenilestari.Response.ResponseCekOtp
import com.importa.ami.Remote.SharedPreferenceID
import com.importa.ami.Remote.SharedPreferenceLogin
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LupaKataSandi : AppCompatActivity() {
    private lateinit var Loading : ProgressBar
    private lateinit var et_lupaKataSandi : EditText
    private lateinit var btnVerifyEmail : Button
    private var id: Int? = null
    private var otp: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_kata_sandi)

        Loading = findViewById(R.id.loading)
        Loading.visibility = View.GONE
        et_lupaKataSandi = findViewById(R.id.et_emailVerifikasi)
        btnVerifyEmail = findViewById(R.id.btnEmailVerifikasi)

        btnVerifyEmail.setOnClickListener {
            if (et_lupaKataSandi.text.isEmpty()){
                et_lupaKataSandi.error=("Harap isi bidang ini")
                et_lupaKataSandi.requestFocus()
                return@setOnClickListener
            }
            else{
                Loading.visibility = View.VISIBLE
                VerifikasiEmail()
            }
        }
    }

    private fun VerifikasiEmail() {
        Loading.visibility = View.VISIBLE
        val inputCekEmail = et_lupaKataSandi.text.toString()
        val requestBody = RequestCekEmail(email = inputCekEmail)

        ApiClient.create().ApiCekEmail(requestBody).enqueue(object :
            Callback<ResponseCekEmail> {
            override fun onResponse(call: Call<ResponseCekEmail>, response: Response<ResponseCekEmail>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        if (response.code() == 200) {
                            id = responseAPI.id
                            otp = responseAPI.otp
                            Log.d("idVerifikasi","$id")
                            Log.d("otpVerifikasi","$otp")
                            SharedPreferenceID.saveAccountInfo(this@LupaKataSandi, id)
                            startActivity(Intent(this@LupaKataSandi, CekOtpVerifikasiEmail::class.java))
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
                            val alertDialogBuilder = AlertDialog.Builder(this@LupaKataSandi)
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

            override fun onFailure(call: Call<ResponseCekEmail>, t: Throwable) {
                Loading.visibility = View.GONE
                Log.e("Error", t.message, t)
                Toast.makeText(this@LupaKataSandi,"Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
            }

        })
    }
}