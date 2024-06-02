package com.dovi.bylenilestari.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dovi.bylenilestari.FullScreenYoutubeViewActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.dovi.bylenilestari.Response.DataItem3
import com.dovi.bylenilestari.R
import com.dovi.bylenilestari.RecommendedFull

class VideoAdapter_Rekomendasi(private val videoList: List<DataItem3?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_VIDEO = 0
    private val VIEW_TYPE_LOAD_MORE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_VIDEO) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_vidio2, parent, false)
            VideoViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false)
            LoadMoreViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoViewHolder) {
            val videoItem = videoList[position]
            videoItem?.let { holder.bind(it) }
        } else if (holder is LoadMoreViewHolder) {
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, RecommendedFull::class.java)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (videoList.size >= 10) {
            videoList.size + 1
        } else {
            videoList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1 && videoList.size >= 10) {
            VIEW_TYPE_LOAD_MORE
        } else {
            VIEW_TYPE_VIDEO // Tampilkan video
        }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val youtubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.youtubePlayerView)
        private val judulTextView: TextView = itemView.findViewById(R.id.judulVideo)
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
            val maxLines = 25
            judulTextView.maxLines = maxLines
            if (videoItem.judul!!.length > judulTextView.maxLines) {
                judulTextView.text = videoItem.judul.substring(0, judulTextView.maxLines) + "..."
            } else {
                judulTextView.text = videoItem.judul
            }

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

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addLoadMoreItem() {
        if (videoList.isNotEmpty() && videoList.lastOrNull() != null) {
            val tempList = videoList.toMutableList()
            tempList.add(null)
            notifyDataSetChanged()
        }
    }
}