package com.dovi.bylenilestari.Request

import com.google.gson.annotations.SerializedName

data class RequestBodyRegister(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("idJurusan")
	val idJurusan: Int? = null
)
