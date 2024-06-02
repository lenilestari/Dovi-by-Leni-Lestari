package com.dovi.bylenilestari.Response

import com.google.gson.annotations.SerializedName

data class ResponseCekEmail(

	@field:SerializedName("otp")
	val otp: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
