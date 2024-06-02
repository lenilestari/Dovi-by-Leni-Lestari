package com.dovi.bylenilestari.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.dovi.bylenilestari.R
import com.dovi.bylenilestari.Request.RequestCreatedVidio

class dataVideoAdapter(val context: Context, val userList:ArrayList<RequestCreatedVidio>):
    RecyclerView.Adapter<dataVideoAdapter.UserViewHolder>()
{

    inner class UserViewHolder(val view: View):RecyclerView.ViewHolder(view){
        var judul_video: TextView
        var kategori_video:TextView
        var gambar_more: ImageView
        var subKategori: TextView
        var deskripsiVideo : TextView
        var idVideo : TextView

        init {
            judul_video = view.findViewById<TextView>(R.id.judul_video_list_item)
            kategori_video = view.findViewById<TextView>(R.id.kategori_list_item)
            subKategori = view.findViewById<TextView>(R.id.Subkategori_list_item)
            deskripsiVideo = view.findViewById<TextView>(R.id.Deskripsi_list_item)
            idVideo = view.findViewById<TextView>(R.id.idVideo_list_item)
            gambar_more = view.findViewById(R.id.mMenus)
            gambar_more.setOnClickListener { popupMenus(it) }
        }

        private fun popupMenus(view:View) {
            val position = userList[adapterPosition]
            val popupMenus = PopupMenu(context,view)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editText->{
                        val view = LayoutInflater.from(context).inflate(R.layout.add_item,null)
                        val judulVideo = view.findViewById<EditText>(R.id.Et_judul)
                        val KategoriVideo = view.findViewById<Spinner>(R.id.spinner_kategori)
                        val subKategoriVideo = view.findViewById<Spinner>(R.id.spinner_Subkategori)
                        val deskripsiVideo = view.findViewById<EditText>(R.id.Et_deskripsi)
                        val IDVideo = view.findViewById<EditText>(R.id.ET_idVideo)

                        val kategoriAdapter = ArrayAdapter.createFromResource(context, R.array.spinner_kategoriVideo, android.R.layout.simple_spinner_item)
                        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        KategoriVideo.adapter = kategoriAdapter

                        val subKategoriAdapter = ArrayAdapter.createFromResource(context, R.array.spinner_subKategoriVideo, android.R.layout.simple_spinner_item)
                        subKategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        subKategoriVideo.adapter = subKategoriAdapter

                        AlertDialog.Builder(context)
                            .setView(view)
                            .setPositiveButton("Ok") { dialog, _ ->
                                val updatedVideo = userList[adapterPosition].copy(
                                    judul = judulVideo.text.toString(),
                                    kategori = KategoriVideo.selectedItem.toString(),
                                    subKategori = subKategoriVideo.selectedItem.toString(),
                                    deskripsi = deskripsiVideo.text.toString(),
                                    linkVidio = IDVideo.text.toString()
                                )
                                userList[adapterPosition] = updatedVideo
                                notifyDataSetChanged()
                                Toast.makeText(context, "User Information is Edited", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }

                            .setNegativeButton("Cancel"){
                                    dialog,_->
                                dialog.dismiss()

                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.delete->{
                        AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Are you sure delete this Video")
                            .setPositiveButton("Yes"){
                                    dialog,_->
                                userList.removeAt(adapterPosition)
                                notifyDataSetChanged()
                                Toast.makeText(context,"Deleted this Information",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else-> true
                }

            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.list_item,parent,false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newListVideo = userList[position]
        holder.judul_video.text = newListVideo.judul
        holder.kategori_video.text = newListVideo.kategori
        holder.subKategori.text = newListVideo.subKategori
        holder.deskripsiVideo.text = newListVideo.deskripsi
        holder.idVideo.text = newListVideo.linkVidio
        holder.idVideo.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return  userList.size
    }
}