package com.dovi.bylenilestari.Request

import com.google.gson.annotations.SerializedName

data class RequestCreatedVidio (

	@field:SerializedName("subKategori")
	val subKategori: String? = null,

	@field:SerializedName("linkVidio")
	val linkVidio: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("histori")
	val histori: Int? = null,

	@field:SerializedName("judul")
	val judul: String? = null
)
