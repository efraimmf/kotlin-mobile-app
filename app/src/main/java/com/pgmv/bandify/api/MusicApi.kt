package com.pgmv.bandify.api

import com.pgmv.bandify.api.response.SongSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApi {
    @GET("search")
    suspend fun searchSong(
        @Query("q") songTitle: String,
        @Query("fmt") format: String = "json"
    ): SongSearchResponse
}