package com.dovi.bylenilestari

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dovi.bylenilestari.Adapter.VideoAdapter_GridRecommended
import com.dovi.bylenilestari.ApiServices.ApiClient
import com.dovi.bylenilestari.Response.DataItem3
import com.dovi.bylenilestari.Response.ResponseAllVidio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecommendedFull : AppCompatActivity() {
    private lateinit var Loading : ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var dialogShown = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommended_full)

        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
        Loading = findViewById(R.id.loading)
        Loading.visibility = View.VISIBLE

        GridVideoRecommended()

        swipeRefreshLayout.setOnRefreshListener {
            dialogShown = false
            GridVideoRecommended()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun GridVideoRecommended() {
        Loading.visibility = View.VISIBLE
        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        val videos = responseAPI.data
                        videos?.let {  videoList ->
                            val filteredList = videoList.filter { it?.history != 0 }.sortedByDescending { it?.date }
                            val rvVideo = findViewById<GridView>(R.id.gridView_Recommended)
                            val adapterVideo = VideoAdapter_GridRecommended(videoList as List<DataItem3>, this@RecommendedFull)
                            rvVideo?.adapter = adapterVideo
                            Loading.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseAllVidio>, t: Throwable) {
                Toast.makeText(this@RecommendedFull, "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                Loading.visibility = View.GONE
            }
        })

    }
}