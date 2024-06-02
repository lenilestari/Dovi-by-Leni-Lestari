package com.dovi.bylenilestari.Fragment

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dovi.bylenilestari.Adapter.ListJurusan
import com.dovi.bylenilestari.Adapter.VideoAdapter
import com.dovi.bylenilestari.Adapter.VideoAdapter_Rekomendasi
import com.dovi.bylenilestari.Adapter.VideoAdapter_YouLike
import com.dovi.bylenilestari.ApiServices.ApiClient
import com.dovi.bylenilestari.R
import com.dovi.bylenilestari.Response.Data
import com.dovi.bylenilestari.Response.DataItem
import com.dovi.bylenilestari.Response.DataItem3
import com.dovi.bylenilestari.Response.Jurusan
import com.dovi.bylenilestari.Response.ResponseAllUsers
import com.dovi.bylenilestari.Response.ResponseAllVidio
import com.dovi.bylenilestari.Response.ResponseLogin
import com.importa.ami.Remote.SharedPreferenceLogin
import com.lenilestari.doviapp.ApiClient.APIServices
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class Home : Fragment() {

    private lateinit var Loading : ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var dialogShown = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private var videoList: MutableList<DataItem3?> = mutableListOf()
    private val apiService: APIServices = ApiClient.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        Loading = view.findViewById(R.id.loading)
        Loading.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id.RV_listVidio)

        videoAdapter = VideoAdapter(videoList )
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = videoAdapter

        AmbilDataAllVidio()
        YouLike()
        RekomendasiVidio()

        swipeRefreshLayout.setOnRefreshListener {
            dialogShown = false
            AmbilDataAllVidio()
            RekomendasiVidio()
            YouLike()
            swipeRefreshLayout.isRefreshing = false
        }

        ApiNamaJurusan()
        return view
    }

    private fun RekomendasiVidio() {
        Loading.visibility = View.VISIBLE
        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        val videos = responseAPI.data
                        videos?.let { videoList ->
                            val rvVideo = view?.findViewById<RecyclerView>(R.id.RV_listVidio_rekomendasi)
                            rvVideo?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            val adapterVideo = VideoAdapter_Rekomendasi(videoList.take(10) as List<DataItem3?>)
                            if (videoList.size > 10) {
                                adapterVideo.addLoadMoreItem()
                            }
                            rvVideo?.adapter = adapterVideo
                            Loading.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseAllVidio>, t: Throwable) {
                Toast.makeText(requireContext(), "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                Loading.visibility = View.GONE
            }
        })
    }

    private fun YouLike() {
        Loading.visibility = View.VISIBLE
        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                            val videos = responseAPI.data
                            videos?.let { videoList ->
                                val filteredList = videoList.filter { it?.history != 0 }.sortedByDescending { it?.date }
                                val rvVideo = view?.findViewById<RecyclerView>(R.id.RV_listVidio_for_you)
                                rvVideo?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                                val adapterVideo = VideoAdapter_YouLike(filteredList.take(10) as List<DataItem3?>)
                                if (videoList.size > 10) {
                                    adapterVideo.addLoadMoreItem()
                                }
                                rvVideo?.adapter = adapterVideo
                                Loading.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseAllVidio>, t: Throwable) {
                Toast.makeText(requireContext(), "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                Loading.visibility = View.GONE
            }
        })
    }

    private fun AmbilDataAllVidio() {
        Loading.visibility = View.VISIBLE
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -10)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sepuluhHari = dateFormat.format(cal.time)

        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        val videos = responseAPI.data
                        videos?.let { videoList ->
                            val filteredList = videoList.filter { it?.date?.compareTo(sepuluhHari) ?: 1 >= 0 }
                                .sortedByDescending { it?.date }
                            val rvVideo = view?.findViewById<RecyclerView>(R.id.RV_listVidio)
                            rvVideo?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            val adapterVideo = VideoAdapter(ArrayList<DataItem3?>() )
                            adapterVideo.setData(filteredList as List<DataItem3>)
                            rvVideo?.adapter = adapterVideo
                            Loading.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseAllVidio>, t: Throwable) {
                Toast.makeText(requireContext(), "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                Loading.visibility = View.GONE
            }
        })
    }

    private fun ApiNamaJurusan() {
        Loading.visibility = View.VISIBLE
        val savedEmail = SharedPreferenceLogin.getEmail(requireContext())
        savedEmail?.let { email ->
            ApiClient.create().getDataNamaJurusan(email).enqueue(object : Callback<ResponseAllUsers> {
                override fun onResponse(call: Call<ResponseAllUsers>, response: Response<ResponseAllUsers>) {
                    if (response.isSuccessful) {
                        val responseAPI = response.body()
                        responseAPI?.let {
                            val users = responseAPI.data
                            users?.let { userList ->
                                val filteredUsers = userList.filter { it?.email == savedEmail }
                                val rvNamaJurusan = view?.findViewById<RecyclerView>(R.id.RV_namaJurusan)
                                rvNamaJurusan?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                val adapterNamaJurusan = ListJurusan(filteredUsers as List<DataItem>)
                                rvNamaJurusan?.adapter = adapterNamaJurusan
                                Loading.visibility = View.GONE
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseAllUsers>, t: Throwable) {
                    Toast.makeText(requireContext(), "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                    Loading.visibility = View.GONE
                }
            })
        } ?: run {
            Toast.makeText(requireContext(), "Email tidak tersimpan", Toast.LENGTH_SHORT).show()
            Loading.visibility = View.GONE
        }
    }
    private fun showPopUpDialog(title:String, message: String) {
        val alertDialogBuilder = android.app.AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}