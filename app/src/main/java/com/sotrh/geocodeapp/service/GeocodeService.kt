package com.sotrh.geocodeapp.service

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.sotrh.geocodeapp.model.AddressResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by benjamin on 10/11/17
 */
object GeocodeService  {

    private val GEOCODE_API_KEY = "AIzaSyD4Fw--7VDwxLcGl8Dqal2gsLogo4thwU0"
    private val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    private val api = retrofit.create(API::class.java)

    private interface API {
        @GET("json")
        fun getResultsWithAddress(@Query("address") address: String, @Query("key") key: String): Single<AddressResponse>
    }

    fun getResultsWithAddress(address: String) = api.getResultsWithAddress(address, GEOCODE_API_KEY)
}