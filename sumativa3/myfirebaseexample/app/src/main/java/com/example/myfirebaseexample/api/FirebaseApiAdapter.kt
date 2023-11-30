package com.example.myfirebaseexample.api

import com.example.myfirebaseexample.api.response.PostResponse
import com.example.myfirebaseexample.api.response.ComputerResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FirebaseApiAdapter {
    private var URL_BASE = "https://appandroid-a46d3-default-rtdb.firebaseio.com/"
    private val firebaseApi = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getComputers(): MutableMap<String, ComputerResponse>? {
        val call = firebaseApi.create(FirebaseApi::class.java).getComputers().execute()
        val computers = call.body()
        return computers
    }

    fun getComputer(id: String): ComputerResponse? {
        val call = firebaseApi.create(FirebaseApi::class.java).getComputer(id).execute()
        val computer = call.body()
        id.also { computer?.id = it }
        return computer
    }

    fun setComputer(computer: ComputerResponse): PostResponse? {
        val call = firebaseApi.create(FirebaseApi::class.java).setComputer(computer).execute()
        val results = call.body()
        return results
    }
}