package com.nemo.tmdb.utils

object Utils {
    var api_key: String = "0f001a650fdad6834cc5fdc38fb2ed03"
    val api_img_root: String = "https://image.tmdb.org/t/p/w500/"
    val api_img_root2: String = "https://image.tmdb.org/t/p/w300/"
    val api_root: String = "https://api.themoviedb.org/3/"

    fun getApiKey() : String {
        return api_key
    }

    fun getApiRoot(): String {
        return api_root
    }

    fun getImgRoot(): String {
        return api_img_root
    }

    fun getImgRoot2(): String {
        return api_img_root
    }

}
