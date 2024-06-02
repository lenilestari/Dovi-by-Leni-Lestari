package com.dovi.bylenilestari.Request

import com.google.gson.annotations.SerializedName

data class RequestBodyLogin(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
