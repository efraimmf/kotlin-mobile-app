package com.pgmv.bandify.api.response

import com.squareup.moshi.Json

data class SongSearchResponse(
    @Json(name = "data") val data: List<Recording>
)

data class Recording(
    @Json(name = "title") val title: String,
    @Json(name = "artist") val artist: Artist,
    @Json(name = "duration") val duration: Int
)

data class Artist(
    @Json(name = "name") val name: String
)