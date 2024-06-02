package com.dovi.bylenilestari.Response

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Data(

	@field:SerializedName("linkLinkedIn")
	val linkLinkedIn: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("jurusan")
	val jurusan: Jurusan? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("linkInstagram")
	val linkInstagram: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)

data class Jurusan(

	@field:SerializedName("jurusan")
	val jurusan: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
