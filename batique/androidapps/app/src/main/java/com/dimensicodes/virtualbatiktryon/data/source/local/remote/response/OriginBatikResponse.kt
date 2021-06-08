package com.dimensicodes.virtualbatiktryon.data.source.local.remote.response


import com.google.gson.annotations.SerializedName

data class OriginBatikResponse(

    @field:SerializedName("origin")
    val origin: List<OriginItem?>? = null
)


data class OriginItem(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("name")
    val name: String? = null

)
