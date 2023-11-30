package com.example.myfirebaseexample.api.response

import com.google.gson.annotations.SerializedName

data class ComputerResponse(
    @SerializedName("00_Id") var id: String,
    @SerializedName("01_Nombre") var name: String,
    @SerializedName("14_Descripcion") var description: String,
    @SerializedName("16_Costo") var cost: Long,
    @SerializedName("20_Marca") var brand: String
)
