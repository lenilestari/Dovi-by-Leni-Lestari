package com.dovi.bylenilestari

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dovi.bylenilestari.ApiServices.ApiClient
import com.dovi.bylenilestari.Fragment.Watchlist
import com.dovi.bylenilestari.Request.RequestGantiPassword
import com.dovi.bylenilestari.Request.RequestTontonNanti
import com.dovi.bylenilestari.Response.ResponseGantiPassword
import com.dovi.bylenilestari.Response.ResponseTontonNanti
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.importa.ami.Remote.SharedPreferenceID
import com.importa.ami.Remote.SharedPreferenceID_video
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FullScreenYoutubeViewActivity : AppCompatActivity() {

    private lateinit var youTubePlayer: YouTubePlayer
    private var isFullScreen = false
    private lateinit var Loading : ProgressBar
    private lateinit var btnTambahFavorite : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_youtube_view)

        fun formatTangal(date: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            return outputFormat.format(parsedDate ?: Date())
        }

        btnTambahFavorite = findViewById(R.id.btnTambahFavorite)
        btnTambahFavorite.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            val subKategori = intent.getStringExtra("SUB_KATEGORI")
            val judul = intent.getStringExtra("JUDUL")
            val kategori = intent.getStringExtra("KATEGORI")
            val tanggalVidio = intent.getStringExtra("TANGGAL")
            val videoId = intent.getIntExtra("ID", 0)

            judul?.let { title ->
                val tvJudul = view.findViewById<TextView>(R.id.tv_judulVideo)
                tvJudul.text = title
            }
            subKategori?.let { title ->
                val tvSubKategori = view.findViewById<TextView>(R.id.tv_subkategori_detail)
                tvSubKategori.text = title
            }
            kategori?.let { title ->
                val tvKategori = view.findViewById<TextView>(R.id.tv_kategori_detail)
                tvKategori.text = title
            }
            tanggalVidio?.let { title ->
                val tvTanggal = view.findViewById<TextView>(R.id.tv_tanggal_detail)
                tvTanggal.text = formatTangal(title)
            }

            val btnClose = view.findViewById<Button>(R.id.btnSelesai)
            btnClose.setOnClickListener {
                addHistory()
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }

        Loading = findViewById(R.id.loading)
        Loading.visibility = View.VISIBLE
        val youtubePlayerView = findViewById<YouTubePlayerView>(R.id.youtubePlayerView)
        val fullScreenContainer = findViewById<ViewGroup>(R.id.fullScreenContainer)
        youtubePlayerView.enableAutomaticInitialization = false
        lifecycle.addObserver(youtubePlayerView)
        val videoLink = intent.getStringExtra("VIDEO_LINK")
        val subKategori = intent.getStringExtra("SUB_KATEGORI")
        val judul = intent.getStringExtra("JUDUL")
        val kategori = intent.getStringExtra("KATEGORI")
        val deskripsi = intent.getStringExtra("DESKRIPSI")
        val tanggalVidio = intent.getStringExtra("TANGGAL")
        val id = intent.getStringExtra("ID")

        Log.d("DATA_INTENT", "VIDEO_LINK: $videoLink")
        Log.d("DATA_INTENT", "SUB_KATEGORI: $subKategori")
        Log.d("DATA_INTENT", "JUDUL: $judul")
        Log.d("DATA_INTENT", "KATEGORI: $kategori")
        Log.d("DATA_INTENT", "DESKRIPSI: $deskripsi")
        Log.d("DATA_INTENT", "TANGGAL: $tanggalVidio")
        Log.d("DATA_INTENT", "ID: $id")

        val onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (isFullScreen){
                    youTubePlayer.toggleFullscreen()
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        youtubePlayerView.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                isFullScreen = true
                fullScreenContainer.visibility = View.VISIBLE
                fullScreenContainer.addView(fullscreenView)

                WindowInsetsControllerCompat(window!!,findViewById(R.id.rootView)).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }

                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                }
            }

            override fun onExitFullscreen() {
                isFullScreen = false
                fullScreenContainer.visibility = View.GONE
                fullScreenContainer.removeAllViews()

                // status bar and navigation bar
                WindowInsetsControllerCompat(window!!,findViewById(R.id.rootView)).apply {
                    show(WindowInsetsCompat.Type.systemBars())
                }

                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR){
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                }
            }
        })

        val youtubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                Loading.visibility = View.VISIBLE
                youTubePlayer = player
                videoLink?.let { youTubePlayer.cueVideo(it, 0f) }
                judul?.let { title ->
                    val tvJudul = findViewById<TextView>(R.id.tv_judul)
                    tvJudul.text = title
                }
                kategori?.let { kategoriVidio ->
                    val tvkategori = findViewById<TextView>(R.id.tv_kategori)
                    tvkategori.text= kategoriVidio
                }
                deskripsi?.let { deskripsiVideo ->
                    val tvDeskripsi = findViewById<TextView>(R.id.tv_deskripsi)
                    tvDeskripsi.text= deskripsiVideo
                }
                subKategori?.let { subKategori ->
                    val tvSubKategori = findViewById<TextView>(R.id.tv_subKategori)
                    tvSubKategori.text= subKategori
                }
                tanggalVidio?.let { tanggal ->
                    val tvTanggalVidio = findViewById<TextView>(R.id.tv_tanggalVideo)
                    tvTanggalVidio.text= formatTangal(tanggal)
                }
                Loading.visibility = View.GONE
            }
        }

        val iFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(1)
            .fullscreen(1)
            .build()

        youtubePlayerView.enableAutomaticInitialization = false
        youtubePlayerView.initialize(youtubePlayerListener, iFramePlayerOptions)
    }

    private fun addHistory() {
        Loading.visibility = View.VISIBLE
        val Id_video = 1
        val requestBody = RequestTontonNanti(history = Id_video)
        val idValue = intent.getIntExtra("ID", 0)
        Log.d("idValue", "$idValue")
        Log.d("requestBody", "$requestBody")

        ApiClient.create().ApiTontonNanti(idValue, requestBody).enqueue(object :
            Callback<ResponseTontonNanti> {
            override fun onResponse(call: Call<ResponseTontonNanti>, response: Response<ResponseTontonNanti>) {
                if (response.isSuccessful) {
                    val responseAPI = response.body()
                    responseAPI?.let {
                        if (it.status == 200) {
//                            showPopUpDialog("Watchlist","Video Favorite anda Berhasil ditambahkan ke Watchlist")
                            Toast.makeText(this@FullScreenYoutubeViewActivity,"Video Favorite anda berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    try{
                        val errorJson = JSONObject(errorMsg)
                        val message = errorJson.optString("message", "")
                        val status = errorJson.optString("status", "")

                        if (status == "400"){
                            val alertDialogBuilder = AlertDialog.Builder(this@FullScreenYoutubeViewActivity)
                            alertDialogBuilder.setTitle("Periksa kembali")
                            alertDialogBuilder.setMessage("$message")
                            alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            val alertDialog = alertDialogBuilder.create()
                            alertDialog.show()
                        }else{

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Loading.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseTontonNanti>, t: Throwable) {
                Loading.visibility = View.GONE
                Log.e("Error", t.message, t)
                Toast.makeText(this@FullScreenYoutubeViewActivity,"Periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showPopUpDialog(title:String, message: String) {
        val alertDialogBuilder = android.app.AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (!isFullScreen){
                youTubePlayer.toggleFullscreen()
            }
        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if (isFullScreen){
                youTubePlayer.toggleFullscreen()
            }
        }
    }
}