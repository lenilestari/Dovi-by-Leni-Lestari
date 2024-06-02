package com.dovi.bylenilestari.Request

import com.google.gson.annotations.SerializedName

data class RequestGantiPassword(

	@field:SerializedName("password")
	val password: String? = null
)
