package com.dovi.bylenilestari.Response

import com.google.gson.annotations.SerializedName

data class ResponseTontonNanti(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
