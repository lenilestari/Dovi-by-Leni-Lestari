package com.dovi.bylenilestari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dovi.bylenilestari.ApiServices.ApiClient
import com.dovi.bylenilestari.Request.RequestBodyLogin
import com.dovi.bylenilestari.Request.RequestBodyRegister
import com.dovi.bylenilestari.Response.ResponseLogin
import com.dovi.bylenilestari.Response.ResponseRegister
import com.importa.ami.Remote.SharedPreferenceLogin
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {
    private lateinit var checkPassword : CheckBox
    private lateinit var et_password_register : EditText
    private lateinit var et_email_register : EditText
    private lateinit var et_nama_register : EditText
    private lateinit var btn_register : Button
    private lateinit var Loading : ProgressBar
//    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loginKembali : TextView
    var dialogShown = false
    private lateinit var spinnerJurusan : Spinner
    private var idJurusanSpinner: Int? = null
    private lateinit var dialog : androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
        checkPassword = findViewById(R.id.chck_password_register)
        et_password_register = findViewById(R.id.et_passwordReg)
        et_email_register = findViewById(R.id.et_emailReg)
        et_nama_register = findViewById(R.id.et_namaReg)
        btn_register = findViewById(R.id.btnRegister)
        Loading = findViewById(R.id.loading)
        Loading.visibility = View.GONE
        loginKembali = findViewById(R.id.tv_loginAkun)

        checkPassword.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                et_password_register.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                et_password_register.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        btn_register.setOnClickListener {
            if (et_nama_register.text.isEmpty()){
                et_nama_register.error=("Harap isi bidang ini")
                et_nama_register.requestFocus()
                return@setOnClickListener
            }
            if (et_email_register.text.isEmpty()){
                et_email_register.error=("Harap isi bidang ini")
                et_email_register.requestFocus()
                return@setOnClickListener
            }
            if (et_password_register.text.isEmpty()){
                et_password_register.error=("Harap isi bidang ini")
                et_password_register.requestFocus()
            return@setOnClickListener
        }
            else{
                Loading.visibility = View.VISIBLE
                apiRegister()
            }
        }

        loginKembali.setOnClickListener {
            startActivity(Intent(this@Register, Login::class.java))
            finish()
        }

        spinnerJurusan = findViewById(R.id.spinner_jurusan)
        if (spinnerJurusan != null) {
            val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_jurusan,
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerJurusan.adapter = adapter

            spinnerJurusan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    var selectedItem = parent.getItemAtPosition(position).toString()

                    // Tetapkan nilai idJurusan berdasarkan item yang dipilih
                    idJurusanSpinner = when (selectedItem) {
                        "Manajemen Teknik Studio Produksi" -> 1
                        "Manajemen Produksi Berita" -> 2
                        "Manajemen Produksi Siaran" -> 3
                        "Teknologi Permainan" -> 4
                        "Animasi" -> 5
                        "Manajemen Informasi dan Komunikasi" -> 6
                        else -> null
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

//        swipeRefreshLayout.setOnRefreshListener {
//            dialogShown = false
//            apiRegister()
//            swipeRefreshLayout.isRefreshing = false
//        }
    }

    private fun apiRegister() {
        Loading.visibility = View.VISIBLE
        val inputEmail = et_email_register.text.toString()
        val inputPassword = et_password_register.text.toString()
        val inputNama = et_nama_register.text.toString()
        val requestBody = RequestBodyRegister(email = inputEmail, password = inputPassword, nama = inputNama, idJurusan =  idJurusanSpinner)

        ApiClient.create().ApiRegister(requestBody).enqueue(object :
            Callback<ResponseRegister> {
            override fun onResponse(call: Call<ResponseRegister>, response: Response<ResponseRegister>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        if (response.code() == 200) {
                            if (!isDestroyed && !isFinishing) {
                               showDialogRegister("Registrasi anda telah berhasil, silakan masukan kode otp","Register Berhasil")
                            }
//                            startActivity(Intent(this@Register, Login::class.java))
//                            finish()
                            Loading.visibility = View.GONE
                        }
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    try {
                        val errorJson = JSONObject(errorMsg)
                        val messageObject = errorJson.optJSONObject("message")
                        val errorMessage = messageObject?.optString("Message", "")

                        if (!errorMessage.isNullOrEmpty()) {
                            val alertDialogBuilder = AlertDialog.Builder(this@Register)
                            alertDialogBuilder.setTitle("Periksa kembali")
                            alertDialogBuilder.setMessage(errorMessage)
                            alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            val alertDialog = alertDialogBuilder.create()
                            alertDialog.show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    Loading.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                Loading.visibility = View.GONE
                Log.e("Error", t.message, t)
                Toast.makeText(this@Register,"Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun showDialogRegister (message: String, title : String) {
        if (!isDestroyed && !isFinishing) {
            val alertDialogBuilder = AlertDialog.Builder(this@Register)
            alertDialogBuilder.setTitle(title)
            alertDialogBuilder.setMessage(message)
            alertDialogBuilder.setOnDismissListener {
                dialog.dismiss()
                finish()
                val refreshIntent = Intent(this@Register, OTP::class.java)
                refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(refreshIntent)
            }
            alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
                val refreshIntent = Intent(this@Register, OTP::class.java)
                refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(refreshIntent)
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } else {
            Log.d("RegisterActivity", "Activity is destroyed or finishing, cannot show dialog")
        }
    }
}