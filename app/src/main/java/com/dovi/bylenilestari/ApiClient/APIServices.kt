package com.lenilestari.doviapp.ApiClient

import com.dovi.bylenilestari.Request.RequestBodyLogin
import com.dovi.bylenilestari.Request.RequestBodyRegister
import com.dovi.bylenilestari.Request.RequestCekEmail
import com.dovi.bylenilestari.Request.RequestCekOtp
import com.dovi.bylenilestari.Request.RequestCreatedVidio
import com.dovi.bylenilestari.Request.RequestGantiPassword
import com.dovi.bylenilestari.Request.RequestTontonNanti
import com.dovi.bylenilestari.Request.RequestUpdateVideo
import com.dovi.bylenilestari.Response.ResponseAllUsers
import com.dovi.bylenilestari.Response.ResponseAllVidio
import com.dovi.bylenilestari.Response.ResponseCekEmail
import com.dovi.bylenilestari.Response.ResponseCekOtp
import com.dovi.bylenilestari.Response.ResponseCreatedVidio
import com.dovi.bylenilestari.Response.ResponseGantiPassword
import com.dovi.bylenilestari.Response.ResponseLogin
import com.dovi.bylenilestari.Response.ResponseRegister
import com.dovi.bylenilestari.Response.ResponseTontonNanti
import com.dovi.bylenilestari.Response.ResponseUpateVideo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface APIServices {
    @Headers("Content-Type: application/json")
    @POST("logIn")
    fun APiLogin (@Body requestBody: RequestBodyLogin): Call<ResponseLogin>

    @Headers("Content-Type: application/json")
    @GET("users")
    fun getDataNamaJurusan(@Query("email") email: String): Call<ResponseAllUsers>

    @Headers("Content-Type: application/json")
    @POST("register")
    fun ApiRegister (@Body requestBody: RequestBodyRegister): Call<ResponseRegister>

    @Headers("Content-Type: application/json")
    @POST("cekOtp")
    fun ApiCekOtp (@Body requestBody: RequestCekOtp): Call<ResponseCekOtp>

    @Headers("Content-Type: application/json")
    @POST("cekEmail")
    fun ApiCekEmail (@Body requestBody: RequestCekEmail): Call<ResponseCekEmail>

    @Headers("Content-Type: application/json")
    @POST("createdVidio/{id}")
    fun ApiCreatedVidio (
        @Path("id") id: Int,
        @Body request_CreatedVidio: RequestCreatedVidio
    ): Call <ResponseCreatedVidio>

    @Headers("Content-Type: application/json")
    @POST("changePassword/{id}")
    fun ApiGantiPassword (
        @Path("id") id: Int,
        @Body requestGantiPassword: RequestGantiPassword
    ): Call <ResponseGantiPassword>

    @Headers("Content-Type: application/json")
    @PUT("tontonNanti/{id}")
    fun ApiTontonNanti (
        @Path("id") id: Int,
        @Body requestTontonNanti: RequestTontonNanti
    ): Call <ResponseTontonNanti>

    @Headers("Content-Type: application/json")
    @GET("vidio")
    fun ApiAllVidio(): Call<ResponseAllVidio>

    @Headers("Content-Type: application/json")
    @PUT("updateVidio/{id}")
    fun ApiUpdateVidio (
        @Path("id") id: Int,
        @Body request_updateVidio: RequestUpdateVideo
    ): Call <ResponseUpateVideo>

    @Headers("Content-Type: application/json")
    @DELETE("deleteVidio/{id}")
    fun ApiDeleteVideo(@Path("id") id: Int): Call<ResponseBody>

}


