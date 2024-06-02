package com.dovi.bylenilestari.Response

import com.google.gson.annotations.SerializedName

data class ResponseGantiPassword(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
