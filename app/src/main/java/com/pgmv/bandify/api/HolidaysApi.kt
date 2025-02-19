package com.pgmv.bandify.api

import com.pgmv.bandify.api.response.Holiday
import retrofit2.http.GET
import retrofit2.http.Path

interface HolidaysApi {
    @GET("feriados/v1/{ano}")
    suspend fun getHolidays(
        @Path("ano") year: Int,
    ): List<Holiday>
}