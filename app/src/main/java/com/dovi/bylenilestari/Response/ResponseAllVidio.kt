package com.dovi.bylenilestari.Response

import com.google.gson.annotations.SerializedName

data class ResponseAllVidio (

	@field:SerializedName("data")
	val data: List<DataItem3?>? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

)

data class User(

	@field:SerializedName("linkLinkedIn")
	val linkLinkedIn: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("jurusan")
	val jurusan: Jurusan3? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("linkInstagram")
	val linkInstagram: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)

data class Jurusan3(

	@field:SerializedName("jurusan")
	val jurusan: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class DataItem3(

	@field:SerializedName("subKategori")
	val subKategori: String? = null,

	@field:SerializedName("linkVidio")
	val linkVidio: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("history")
	val history: Int? = null,

	@field:SerializedName("judul")
	val judul: String? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("date")
	val date: String? = null,
)
