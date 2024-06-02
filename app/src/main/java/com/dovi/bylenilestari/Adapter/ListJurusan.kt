package com.dovi.bylenilestari.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dovi.bylenilestari.R
import com.dovi.bylenilestari.Response.DataItem

class ListJurusan (private var listNamaJurusan: List<DataItem>) :
    RecyclerView.Adapter<ListJurusan.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_nama_jurusan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listNamaJurusan[position]
        holder.tvNama.text = item.nama
        holder.tvJurusan.text = item.jurusan?.jurusan2 ?: ""
    }

    override fun getItemCount(): Int {
        return listNamaJurusan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.TV_Nama)
        val tvJurusan: TextView = itemView.findViewById(R.id.TV_Jurusan)
    }
}
