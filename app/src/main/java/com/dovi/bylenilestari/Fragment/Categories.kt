package com.dovi.bylenilestari.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dovi.bylenilestari.Adapter.VideoAdapter
import com.dovi.bylenilestari.Adapter.VideoAdapter_Radio
import com.dovi.bylenilestari.Adapter.VideoAdapter_Rekomendasi
import com.dovi.bylenilestari.Adapter.VideoAdapter_Televisi
import com.dovi.bylenilestari.Adapter.VideoAdapter_YouLike
import com.dovi.bylenilestari.ApiServices.ApiClient
import com.dovi.bylenilestari.R
import com.dovi.bylenilestari.Radio
import com.dovi.bylenilestari.Response.DataItem3
import com.dovi.bylenilestari.Response.ResponseAllVidio
import com.dovi.bylenilestari.Televisi
import com.lenilestari.doviapp.ApiClient.APIServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Categories : Fragment() {
    private lateinit var Loading : ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var dialogShown = false
    private lateinit var iv_Televisi : ImageView
    private lateinit var iv_Radio : ImageView
    private val apiService: APIServices = ApiClient.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        Loading = view.findViewById(R.id.loading)
        Loading.visibility = View.VISIBLE
        iv_Televisi = view.findViewById(R.id.iv_Televisi)
        iv_Televisi.setOnClickListener {
            val intent = Intent(requireContext(), Televisi::class.java)
            startActivity(intent)
        }
        iv_Radio = view.findViewById(R.id.iv_Radio)
        iv_Radio.setOnClickListener {
            val intent = Intent(requireContext(), Radio::class.java)
            startActivity(intent)
        }

        swipeRefreshLayout.setOnRefreshListener {
            dialogShown = false
            videoTelevisi()
            videoRadio()
            swipeRefreshLayout.isRefreshing = false
        }

        videoTelevisi()
        videoRadio()
        return view
    }

    private fun videoRadio() {
        Loading.visibility = View.VISIBLE
        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        val videos = responseAPI.data
                        videos?.let { videoList ->
                            val filteredList = videoList.filter { it?.kategori.equals("radio", ignoreCase = true) }.sortedByDescending { it?.date }
                            val rvVideo = view?.findViewById<RecyclerView>(R.id.RV_listVidio_radio)
                            rvVideo?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            val adapterVideo = VideoAdapter_Radio(filteredList.take(10) as List<DataItem3?>, apiService = apiService)
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

    private fun videoTelevisi() {
        Loading.visibility = View.VISIBLE
        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        val videos = responseAPI.data
                        videos?.let { videoList ->
                            val filteredList = videoList.filter { it?.kategori.equals("televisi", ignoreCase = true) }?.sortedByDescending { it?.date }
                            val rvVideo = view?.findViewById<RecyclerView>(R.id.RV_listVidio_televisi)
                            rvVideo?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            val adapterVideo = VideoAdapter_Televisi(filteredList?.take(10) as List<DataItem3?>, apiService = apiService)
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
}