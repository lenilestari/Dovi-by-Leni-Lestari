package com.dovi.bylenilestari

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dovi.bylenilestari.Adapter.VideoAdapter_Radio
import com.dovi.bylenilestari.Adapter.VideoAdapter_Televisi
import com.dovi.bylenilestari.ApiServices.ApiClient
import com.dovi.bylenilestari.Request.RequestCreatedVidio
import com.dovi.bylenilestari.Response.DataItem3
import com.dovi.bylenilestari.Response.ResponseAllVidio
import com.dovi.bylenilestari.Response.ResponseCreatedVidio
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.importa.ami.Remote.SharedPreferenceID
import com.lenilestari.doviapp.ApiClient.APIServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Radio : AppCompatActivity() {
    private lateinit var Loading : ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var dialogShown = false
    private lateinit var tv_hiburan : TextView
    private lateinit var tv_berita : TextView
    private lateinit var tv_animasi : TextView
    private lateinit var btnTambahVideo: FloatingActionButton
    private lateinit var rv_dataVideo: RecyclerView
    private lateinit var dataVideoList: ArrayList<RequestCreatedVidio>
    private val apiService: APIServices = ApiClient.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio)

        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
        Loading = findViewById(R.id.loading)
        Loading.visibility = View.VISIBLE
        tv_hiburan = findViewById(R.id.tv_hiburan)
        tv_berita = findViewById(R.id.tv_berita)
        tv_animasi = findViewById(R.id.tv_animasi)

        dataVideoList = ArrayList()
        btnTambahVideo = findViewById(R.id.btnTambahVideo_floating)
        rv_dataVideo = findViewById(R.id.rv_tambah_video)
        btnTambahVideo.setOnClickListener {
            tambahDataVideo()
        }

        videoRadio_Hiburan()
        videoRadio_Berita()
        videoRadio_Animasi()
        swipeRefreshLayout.setOnRefreshListener {
            dialogShown = false
            videoRadio_Hiburan()
            videoRadio_Berita()
            videoRadio_Animasi()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun tambahDataVideo() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item, null)

        val judulVideoET = view.findViewById<EditText>(R.id.Et_judul)
        val kategoriVideoSP = view.findViewById<Spinner>(R.id.spinner_kategori)
        val subKategoriVideoSP = view.findViewById<Spinner>(R.id.spinner_Subkategori)
        val deskripsiVideoET = view.findViewById<EditText>(R.id.Et_deskripsi)
        val idVideoET = view.findViewById<EditText>(R.id.ET_idVideo)

        // Pengaturan Spinner
        setupSpinner(kategoriVideoSP, R.array.spinner_kategoriVideo)
        setupSpinner(subKategoriVideoSP, R.array.spinner_subKategoriVideo)

        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(view)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val judulVideo = judulVideoET.text.toString()
            val kategoriVideo = kategoriVideoSP.selectedItem?.toString() ?: "Default Value"
            Log.d("AddVideo", "Kategori saat OK: $kategoriVideo")
            val subKategoriVideo = subKategoriVideoSP.selectedItem?.toString() ?: ""
            val deskripsiVideo = deskripsiVideoET.text.toString()
            val idVideo = idVideoET.text.toString()

            val requestBody = RequestCreatedVidio(
                judul = judulVideo,
                kategori = kategoriVideo,
                subKategori = subKategoriVideo,
                deskripsi = deskripsiVideo,
                linkVidio = idVideo
            )

            val idValue = SharedPreferenceID.getID(this@Radio)
            ApiClient.create().ApiCreatedVidio(idValue, requestBody).enqueue(object :
                Callback<ResponseCreatedVidio> {
                override fun onResponse(
                    call: Call<ResponseCreatedVidio>,
                    response: Response<ResponseCreatedVidio>
                ) {
                    if (response.isSuccessful) {
                        val responseAPI = response.body()
                        responseAPI?.let {
                            if (it.status == 200) {
                                dataVideoList.add(requestBody)
//                                dataVideoAdapter.notifyDataSetChanged()
                                Toast.makeText(this@Radio, "Video berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@Radio, "Gagal menambahkan video: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@Radio, "Gagal menambahkan video: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseCreatedVidio>, t: Throwable) {
                    // Tampilkan pesan jika terjadi error
                    Log.e("Error", t.message, t)
                    Toast.makeText(
                        this@Radio,
                        "Periksa koneksi internet anda",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }
        addDialog.create().show()
    }

    private fun videoRadio_Animasi() {
        Loading.visibility = View.VISIBLE
        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        val videos = responseAPI.data
                        videos?.let { videoList ->
                            val filteredList = videoList.filter {
                                it?.kategori.equals("radio", ignoreCase = true) &&
                                        it?.subKategori.equals("animasi", ignoreCase = true)
                            }.sortedByDescending { it?.date }
                            if (filteredList.isNotEmpty()) {
                                val rvVideo = findViewById<RecyclerView>(R.id.RV_listVidio_animasi)
                                rvVideo.layoutManager = LinearLayoutManager(this@Radio, LinearLayoutManager.HORIZONTAL, false)
                                val adapterVideo = VideoAdapter_Radio(filteredList as MutableList<DataItem3?>, apiService = apiService)
                                rvVideo.adapter = adapterVideo
                                Loading.visibility = View.GONE
                            } else {
                                tv_animasi.visibility = View.GONE
                                Toast.makeText(this@Radio, "Tidak ada video radio kategori animasi yang tersedia", Toast.LENGTH_SHORT).show()
                                Loading.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@Radio, "Terjadi kesalahan saat mengambil data", Toast.LENGTH_SHORT).show()
                    Loading.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseAllVidio>, t: Throwable) {
                Toast.makeText(this@Radio, "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                Loading.visibility = View.GONE
            }
        })
    }

    private fun videoRadio_Berita() {
        Loading.visibility = View.VISIBLE
        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        val videos = responseAPI.data
                        videos?.let { videoList ->
                            val filteredList = videoList.filter {
                                it?.kategori.equals("radio", ignoreCase = true) &&
                                        it?.subKategori.equals("berita", ignoreCase = true)
                            }.sortedByDescending { it?.date }
                            if (filteredList.isNotEmpty()) {
                                val rvVideo = findViewById<RecyclerView>(R.id.RV_listVidio_berita)
                                rvVideo.layoutManager = LinearLayoutManager(this@Radio, LinearLayoutManager.HORIZONTAL, false)
                                val adapterVideo = VideoAdapter_Radio(filteredList as MutableList<DataItem3?>, apiService = apiService)
                                rvVideo.adapter = adapterVideo
                                Loading.visibility = View.GONE
                            } else {
                                tv_berita.visibility = View.GONE
                                Toast.makeText(this@Radio, "Tidak ada video radio kategori berita yang tersedia", Toast.LENGTH_SHORT).show()
                                Loading.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@Radio, "Terjadi kesalahan saat mengambil data", Toast.LENGTH_SHORT).show()
                    Loading.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseAllVidio>, t: Throwable) {
                Toast.makeText(this@Radio, "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                Loading.visibility = View.GONE
            }
        })
    }

    private fun videoRadio_Hiburan() {
        Loading.visibility = View.VISIBLE
        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        val videos = responseAPI.data
                        videos?.let { videoList ->
                            val filteredList = videoList.filter {
                                it?.kategori.equals("radio", ignoreCase = true) &&
                                        it?.subKategori.equals("hiburan", ignoreCase = true)
                            }.sortedByDescending { it?.date }
                            if (filteredList.isNotEmpty()) {
                                val rvVideo = findViewById<RecyclerView>(R.id.RV_listVidio_hiburan)
                                rvVideo.layoutManager = LinearLayoutManager(this@Radio, LinearLayoutManager.HORIZONTAL, false)
                                val adapterVideo = VideoAdapter_Radio(filteredList as MutableList<DataItem3?>, apiService = apiService)
                                rvVideo.adapter = adapterVideo
                                Loading.visibility = View.GONE
                            } else {
                                tv_hiburan.visibility = View.GONE
                                Toast.makeText(this@Radio, "Tidak ada video radio kategori hiburan yang tersedia", Toast.LENGTH_SHORT).show()
                                Loading.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@Radio, "Terjadi kesalahan saat mengambil data", Toast.LENGTH_SHORT).show()
                    Loading.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseAllVidio>, t: Throwable) {
                Toast.makeText(this@Radio, "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                Loading.visibility = View.GONE
            }
        })
    }

    private fun setupSpinner(spinner: Spinner, arrayResourceId: Int) {
        val adapter = ArrayAdapter.createFromResource(
            this,
            arrayResourceId,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}