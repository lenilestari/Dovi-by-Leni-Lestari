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
import com.dovi.bylenilestari.Request.RequestGantiPassword
import com.dovi.bylenilestari.Response.ResponseCekEmail
import com.dovi.bylenilestari.Response.ResponseGantiPassword
import com.importa.ami.Remote.SharedPreferenceID
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GantiKataSandi : AppCompatActivity() {
    private lateinit var Loading : ProgressBar
    private lateinit var et_gantiPassword : EditText
    private lateinit var btn_verifikasiPassword : Button
    private var id: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_kata_sandi)

        Loading = findViewById(R.id.loading)
        Loading.visibility = View.GONE
        et_gantiPassword = findViewById(R.id.et_emailChangePassword)
        btn_verifikasiPassword = findViewById(R.id.btnChangeEmail)

        btn_verifikasiPassword.setOnClickListener {
            if (et_gantiPassword.text.isEmpty()){
                et_gantiPassword.error=("Harap isi bidang ini")
                et_gantiPassword.requestFocus()
                return@setOnClickListener
            }
            else{
                Loading.visibility = View.VISIBLE
                GantiPassword()
            }
        }
    }

    private fun GantiPassword() {
        Loading.visibility = View.VISIBLE
        val gantiPassword = et_gantiPassword.text.toString()
        val requestBody = RequestGantiPassword(password = gantiPassword)
        val idValue = SharedPreferenceID.getID(this@GantiKataSandi)
        Log.d("idValue", "$idValue")
        Log.d("requestBody", "$requestBody")

        ApiClient.create().ApiGantiPassword(idValue, requestBody).enqueue(object :
            Callback<ResponseGantiPassword> {
            override fun onResponse(call: Call<ResponseGantiPassword>, response: Response<ResponseGantiPassword>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        if (it.status == 200) {
                            startActivity(Intent(this@GantiKataSandi, Login::class.java))
                            finish()
                        }
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    try{
                        val errorJson = JSONObject(errorMsg)
                        val message = errorJson.optString("message", "")
                        val status = errorJson.optString("status", "")

                        if (status == "400"){
                            val alertDialogBuilder = AlertDialog.Builder(this@GantiKataSandi)
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

            override fun onFailure(call: Call<ResponseGantiPassword>, t: Throwable) {
                Loading.visibility = View.GONE
                Log.e("Error", t.message, t)
                Toast.makeText(this@GantiKataSandi,"Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
            }

        })
    }
}