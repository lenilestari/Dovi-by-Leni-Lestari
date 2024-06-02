package com.dovi.bylenilestari.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dovi.bylenilestari.Adapter.VideoAdapter_GridYouLike
import com.dovi.bylenilestari.ApiServices.ApiClient
import com.dovi.bylenilestari.R
import com.dovi.bylenilestari.Response.DataItem3
import com.dovi.bylenilestari.Response.ResponseAllVidio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Watchlist : Fragment() {
    private lateinit var Loading : ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var dialogShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_watchlist, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        Loading = view.findViewById(R.id.loading)
        Loading.visibility = View.VISIBLE

        GridVideoYouLike()

        swipeRefreshLayout.setOnRefreshListener {
            dialogShown = false
            GridVideoYouLike()
            swipeRefreshLayout.isRefreshing = false
        }
        return view
    }

    private fun GridVideoYouLike() {
        Loading.visibility = View.VISIBLE
        ApiClient.create().ApiAllVidio().enqueue(object : Callback<ResponseAllVidio> {
            override fun onResponse(call: Call<ResponseAllVidio>, response: Response<ResponseAllVidio>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        val videos = responseAPI.data
                        videos?.let {  videoList ->
                            val filteredList = videoList.filter { it?.history != 0 }.sortedByDescending { it?.date }
                            val rvVideo = view?.findViewById<GridView>(R.id.gridView_YouLike)
                            val adapterVideo = VideoAdapter_GridYouLike(filteredList as List<DataItem3>, requireContext())
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