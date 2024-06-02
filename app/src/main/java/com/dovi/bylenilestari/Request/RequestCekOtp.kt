package com.dovi.bylenilestari.Request

import com.google.gson.annotations.SerializedName

data class RequestCekOtp(

	@field:SerializedName("otp")
	val otp: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
