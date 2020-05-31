package com.nemo.tmdb.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MoviesResponse {

    open class Movies {
        @SerializedName("results")
        @Expose
        open val results: List<Movie>? = null
    }

    open class Movie {
        @SerializedName("id")
        @Expose
        open val id: Int? = null

        @SerializedName("title")
        @Expose
        open val title: String? = null

        @SerializedName("original_title")
        open val orig_title: String? = null

        @SerializedName("overview")
        open val description: String? = null

        @SerializedName("backdrop_path")
        open val backdrop: String? = null

        @SerializedName("poster_path")
        open val poster: String? = null

        @SerializedName("original_language")
        open val orig_lang: String? = null

        @SerializedName("release_date")
        open val release_date: String? = null

        @SerializedName("popularity")
        open val popularity: Float = 0.0f

        @SerializedName("vote_count")
        open val vote_count: Int = 0

        @SerializedName("video")
        open val video: Boolean = false

        @SerializedName("vote_average")
        @Expose
        open val vote_av: Float = 0.0f

        @SerializedName("genre_ids")
        @Expose
        open val genres: List<Int>? = null
    }
}