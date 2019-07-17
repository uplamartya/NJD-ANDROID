package com.underscoretec.njd

import com.google.gson.JsonElement
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface VideoInterface {
    @Multipart
    @POST("/video")
    fun uploadVideoToServer(@Part video: MultipartBody.Part): Call<JsonElement>
}