package com.pgmv.bandify.api.response

import com.squareup.moshi.Json

data class Holiday(
    @Json(name = "date") val date: String,
    @Json(name = "name") val name: String,
    @Json(name = "type") val type: String
)

