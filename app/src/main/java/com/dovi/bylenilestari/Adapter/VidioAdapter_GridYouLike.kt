package com.dovi.bylenilestari.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dovi.bylenilestari.FullScreenYoutubeViewActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.dovi.bylenilestari.Response.DataItem3
import com.dovi.bylenilestari.R

class VideoAdapter_GridYouLike(private val videoList: List<DataItem3>, private val context: Context) : BaseAdapter() {

    override fun getCount(): Int {
        return videoList.size
    }

    override fun getItem(position: Int): Any {
        return videoList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val viewHolder: VideoViewHolder
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.gridview_item, parent, false)
            viewHolder = VideoViewHolder(view!!)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as VideoViewHolder
        }

        val videoItem = videoList[position]
        viewHolder.bind(videoItem)

        return view
    }

    inner class VideoViewHolder(itemView: View) {
        private val youtubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.iv_videoRecommended)
        private val judulTextView: TextView = itemView.findViewById(R.id.tv_judulRecommended)
        private lateinit var youTubePlayer: YouTubePlayer
        private var videoItem: DataItem3? = null

        init {
            itemView.findViewById<TextView>(R.id.tv_detailRecommended).setOnClickListener {
                videoItem?.let { video ->
                    val intent = Intent(context, FullScreenYoutubeViewActivity::class.java)
                    intent.putExtra("VIDEO_LINK", video.linkVidio)
                    intent.putExtra("SUB_KATEGORI", video.subKategori)
                    intent.putExtra("KATEGORI", video.kategori)
                    intent.putExtra("TANGGAL", video.date)
                    intent.putExtra("JUDUL", video.judul)
                    intent.putExtra("DESKRIPSI", video.deskripsi)
                    intent.putExtra("ID", video.id)
                    context.startActivity(intent)
                }
            }
        }

        fun bind(videoItem: DataItem3) {
            this.videoItem = videoItem
            val maxLines = 15
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

            youtubePlayerView.setOnClickListener {
                videoItem?.let { video ->
                    val intent = Intent(context, FullScreenYoutubeViewActivity::class.java).apply {
                        putExtra("SUB_KATEGORI", video.subKategori)
                        putExtra("VIDEO_LINK", video.linkVidio)
                        putExtra("KATEGORI", video.kategori)
                        putExtra("TANGGAL", video.date)
                        putExtra("JUDUL", video.judul)
                        putExtra("DESKRIPSI", video.deskripsi)
                        putExtra("ID", video.id)

                    }
                    context.startActivity(intent)
                }
            }
        }
    }
}