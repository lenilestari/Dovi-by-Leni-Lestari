package com.dovi.bylenilestari.Adapter

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dovi.bylenilestari.FullScreenYoutubeViewActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.dovi.bylenilestari.Response.DataItem3
import com.dovi.bylenilestari.R
import com.dovi.bylenilestari.Request.RequestUpdateVideo
import com.dovi.bylenilestari.Response.ResponseUpateVideo
import com.lenilestari.doviapp.ApiClient.APIServices
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoAdapter(private var videoList: List<DataItem3?>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_vidio_youtube, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoItem = videoList[position]
        videoItem?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun setData(newVideoList: List<DataItem3?>) {
        videoList = newVideoList
        notifyDataSetChanged()
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        private val youtubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.youtubePlayerView)
        private lateinit var youTubePlayer: YouTubePlayer
        private var videoItem: DataItem3? = null


        init {
            itemView.findViewById<TextView>(R.id.tv_detalVidio).setOnClickListener {
                videoItem?.let { video ->
                    val intent = Intent(itemView.context, FullScreenYoutubeViewActivity::class.java)
                    intent.putExtra("VIDEO_LINK", video.linkVidio)
                    intent.putExtra("SUB_KATEGORI", video.subKategori)
                    intent.putExtra("KATEGORI", video.kategori)
                    intent.putExtra("TANGGAL", video.date)
                    intent.putExtra("JUDUL", video.judul)
                    intent.putExtra("DESKRIPSI", video.deskripsi)
                    intent.putExtra("ID", video.id)
                    itemView.context.startActivity(intent)
                }
            }
        }

        fun bind(videoItem: DataItem3) {
            this.videoItem = videoItem

            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    youTubePlayer = player
                    videoItem.linkVidio?.let { youTubePlayer.cueVideo(it, 0f) }
                }
            })

            itemView.setOnClickListener {
                videoItem?.let { video ->
                    val intent = Intent(itemView.context, FullScreenYoutubeViewActivity::class.java).apply {
                        putExtra("SUB_KATEGORI", video.subKategori)
                        putExtra("VIDEO_LINK", video.linkVidio)
                        putExtra("KATEGORI", video.kategori)
                        putExtra("TANGGAL", video.date)
                        putExtra("JUDUL", video.judul)
                        putExtra("DESKRIPSI", video.deskripsi)
                        putExtra("ID", video.id)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}