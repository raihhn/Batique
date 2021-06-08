package com.dimensicodes.virtualbatiktryon.data.source.local.remote.response


import com.google.gson.annotations.SerializedName

data class BatikResponse(

    @field:SerializedName("batik")
    val batik: List<BatikItem?>? = null
)

data class BatikItem(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("origin")
    val origin: List<String?>? = null,

    @field:SerializedName("imagePath")
    val imagePath: String? = null
)
