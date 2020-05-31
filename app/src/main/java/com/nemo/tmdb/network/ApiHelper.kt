package com.nemo.tmdb.network

import com.nemo.tmdb.model.MoviesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiHelper {

    @GET("movie/now_playing")
    fun getMovies(@Query("api_key") api_key: String?) : Observable<MoviesResponse.Movies>

    @GET("search/movie")
    fun searchMovies(@Query("api_key") api_key: String?, @Query("query") query : String?) : Observable<MoviesResponse.Movies>


}