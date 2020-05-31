package com.nemo.tmdb.view

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nemo.tmdb.R
import com.nemo.tmdb.model.Fav
import com.nemo.tmdb.model.FavDB
import com.nemo.tmdb.model.MoviesResponse
import com.nemo.tmdb.utils.Utils
import com.nemo.tmdb.viewmodel.ActivityViewModel
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class Movie2Fragment : Fragment() {
    var mainViewModel : ActivityViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view :View = inflater.inflate(com.nemo.tmdb.R.layout.fragment_movie, container, false)
        Log.d("Movie", "Call onCreateView in Movie2 fragment, bundle")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProviders.of(activity!!).get(ActivityViewModel::class.java)

        var movie : MoviesResponse.Movie? = mainViewModel!!.getCurrMovie()

        if (movie != null) {
            var image_url: String = Utils.getImgRoot() + movie?.backdrop
            Log.d("Adapter", "Image url: "+image_url)

            movie_title.text = movie.title
            Glide.with(context!!).asBitmap().load(image_url).apply(RequestOptions().centerCrop()).into(movie_poster )

            release_date.text = "Released: "+movie.release_date
            movie_rating.text = "Rating: "+movie.vote_av.toString()
            popularity.text = "Popularity: "+movie.popularity.toString()
            description.text = movie.description

            // wstepne ustawienie gwiazdki
            GlobalScope.launch(Dispatchers.IO) {
                var fav: Fav? = FavDB.getInstance(context!!)?.favDao()?.getFav(movie.id!!)
                if (fav != null) {
                    MainScope().launch {
                        favor.setImageResource(R.drawable.ic_star_border_black_24dp)
                    }
                }
            }

            favor.setOnClickListener{view ->
                GlobalScope.launch(Dispatchers.IO) {
                    var fav: Fav? = FavDB.getInstance(context!!)?.favDao()?.getFav(movie.id!!)
                    if (fav != null) {
                        FavDB.getInstance(context!!)?.favDao()?.deleteFav(fav!!)
                        MainScope().launch {
                            (view as ImageView).setImageResource(R.drawable.ic_star_border_black_24dp)
                        }
                    } else {
                        fav = Fav(movie.id!!, movie.title!!)
                        FavDB.getInstance(context!!)?.favDao()?.addFav(fav!!)
                        MainScope().launch {
                            (view as ImageView).setImageResource(R.drawable.ic_star_black_24dp)
                        }
                    }
                }
            }
        }
    }
}