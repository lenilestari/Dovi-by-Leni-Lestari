package com.dovi.bylenilestari.Adapter

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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

class VideoAdapter_Radio(private var videoList: List<DataItem3?>, private val apiService: APIServices) :
    RecyclerView.Adapter<VideoAdapter_Radio.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_vidio, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoItem = videoList[position]
        videoItem?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val youtubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.youtubePlayerView)
        private val judulTextView: TextView = itemView.findViewById(R.id.judulVideo)
        private lateinit var youTubePlayer: YouTubePlayer
        private var videoItem: DataItem3? = null
        private val addMoreButton: ImageView = itemView.findViewById(R.id.add_more)

        init {
            addMoreButton.post {
                addMoreButton.setOnClickListener {
                    Log.d("VideoAdapter", "addMoreButton clicked")
                    videoItem?.let { video ->
                        val idVideo = video.id
                        val idUser = video.user?.id
                        Log.d("VideoAdapter", "ID Video: $idVideo, ID User: $idUser")
                        alertDialogPilihan(video)
                    } ?: run {
                        Log.d("VideoAdapter", "videoItem is null")
                    }
                }

                addMoreButton.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            Log.d("VideoAdapter", "addMoreButton touched")
                        }
                    }
                    false
                }
            }

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

        private fun alertDialogPilihan(video: DataItem3) {
            val options = arrayOf("Edit", "Delete")

            AlertDialog.Builder(itemView.context)
                .setTitle("Edit atau Delete Video")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> showEditVideoDialog(video)
                        1 -> showDeleteConfirmationDialog(video)
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        private fun showEditVideoDialog(video: DataItem3) {
            val view = LayoutInflater.from(itemView.context).inflate(R.layout.add_item, null)
            val judulVideo = view.findViewById<EditText>(R.id.Et_judul)
            val KategoriVideo = view.findViewById<Spinner>(R.id.spinner_kategori)
            val subKategoriVideo = view.findViewById<Spinner>(R.id.spinner_Subkategori)
            val deskripsiVideo = view.findViewById<EditText>(R.id.Et_deskripsi)
            val IDVideo = view.findViewById<EditText>(R.id.ET_idVideo)

            val kategoriAdapter = ArrayAdapter.createFromResource(itemView.context, R.array.spinner_kategoriVideo, android.R.layout.simple_spinner_item)
            kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            KategoriVideo.adapter = kategoriAdapter

            val subKategoriAdapter = ArrayAdapter.createFromResource(itemView.context, R.array.spinner_subKategoriVideo, android.R.layout.simple_spinner_item)
            subKategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            subKategoriVideo.adapter = subKategoriAdapter

            // Populate dialog fields with video data
            judulVideo.setText(video.judul)
            deskripsiVideo.setText(video.deskripsi)
            IDVideo.setText(video.linkVidio)

            AlertDialog.Builder(itemView.context)
                .setView(view)
                .setPositiveButton("Ok") { dialog, _ ->
                    val updatedVideo = video.copy(
                        judul = judulVideo.text.toString(),
                        kategori = KategoriVideo.selectedItem.toString(),
                        subKategori = subKategoriVideo.selectedItem.toString(),
                        deskripsi = deskripsiVideo.text.toString(),
                        linkVidio = IDVideo.text.toString()
                    )

                    // Kirim permintaan pembaruan ke server
                    apiService.ApiUpdateVidio(video.id ?: 0, RequestUpdateVideo(
                        subKategori = updatedVideo.subKategori,
                        idUser = updatedVideo.user?.id ?: 0,
                        linkVidio = updatedVideo.linkVidio,
                        kategori = updatedVideo.kategori,
                        deskripsi = updatedVideo.deskripsi,
                        judul = updatedVideo.judul
                    )
                    ).enqueue(object : Callback<ResponseUpateVideo> {
                        override fun onResponse(call: Call<ResponseUpateVideo>, response: Response<ResponseUpateVideo>) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                responseBody?.let {
                                    if (it.status == 200) {
                                        // Video berhasil diperbarui
                                        videoItem = updatedVideo
                                        notifyDataSetChanged()
                                        Toast.makeText(itemView.context, "Video berhasil diupdate", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(itemView.context, "Gagal update video: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(itemView.context, "Gagal update video: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseUpateVideo>, t: Throwable) {
                            Toast.makeText(itemView.context, "Periksa koneksi internet anda: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

                    dialog.dismiss()

                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        private fun showDeleteConfirmationDialog(video: DataItem3) {
            AlertDialog.Builder(itemView.context)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Apakah kamu yakin ingin menghapus video ini?")
                .setPositiveButton("Yes") { _, _ ->
                    // Kirim permintaan penghapusan ke server
                    apiService.ApiDeleteVideo(video.id ?: 0).enqueue(object :
                        Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                // Video berhasil dihapus
                                videoList = videoList.filter { it?.id != video.id }.toMutableList()
                                notifyDataSetChanged()
                                Toast.makeText(itemView.context, "Video berhasil dihapus", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(itemView.context, "Video gagal dihapus: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(itemView.context, "Periksa koneksi internet anda: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
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
}
