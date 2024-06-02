package com.dovi.bylenilestari.Response

import com.google.gson.annotations.SerializedName

data class ResponseAllUsers(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Jurusan2 (

	@field:SerializedName("jurusan")
	val jurusan2: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class DataItem(

	@field:SerializedName("linkLinkedIn")
	val linkLinkedIn: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("jurusan")
	val jurusan: Jurusan2? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("linkInstagram")
	val linkInstagram: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)
