package com.nemo.tmdb.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.NonNull
import com.nemo.tmdb.model.Fav
import com.nemo.tmdb.model.FavDB
import com.nemo.tmdb.model.MoviesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActivityViewModel(app : Application) : AndroidViewModel(app) {

    val movieLiveData: MutableLiveData<MoviesResponse.Movie> = MutableLiveData<MoviesResponse.Movie>()
    val favsLiveData: MutableLiveData<List<Fav>> = MutableLiveData<List<Fav>>()
    var moviesList : ArrayList<MoviesResponse.Movie>? = ArrayList<MoviesResponse.Movie>()

    private val context = getApplication<Application>().applicationContext

    fun ActivityViewModel(@NonNull application: Application?) {

    }

    override fun onCleared() {
        super.onCleared()
    }

    fun retrieveAllFavsFromDb() {
        GlobalScope.launch(Dispatchers.IO) {
            var favs: List<Fav>? = FavDB.getInstance(context)?.favDao()?.getAllFavs()
            if (favs != null) {
                favsLiveData.postValue(favs)
            }
        }
    }

    fun setCurrMovie(id : Int){
        if (moviesList != null){
            for (movie in moviesList!!)    {
                if (movie.id == id) {
                    movieLiveData.setValue(movie)
                }
            }
        }
    }

    fun setCurrMovie(movie: MoviesResponse.Movie){
        movieLiveData.setValue(movie)
    }

    fun addMovies(resp: MoviesResponse.Movies?) {
         moviesList?.clear()
         if (resp != null) {
             for (movie in resp.results!!) {
                 moviesList!!.add(movie)
             }
         }
    }

    fun getCurrMovie() : MoviesResponse.Movie? {
        if (movieLiveData != null) {
            return movieLiveData.value
        } else {
            return null
        }
    }
}
