package com.example.myfirebaseexample.api

import com.example.myfirebaseexample.api.response.PostResponse
import com.example.myfirebaseexample.api.response.ComputerResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FirebaseApi {
    @GET("Computers.json")
    fun getComputers(): Call<MutableMap<String, ComputerResponse>>

    @GET("Computers/{id}.json")
    fun getComputer(
        @Path("id") id: String
    ): Call<ComputerResponse>

    @POST("Computers.json")
    fun setComputer(
        @Body() body: ComputerResponse
    ): Call<PostResponse>
}