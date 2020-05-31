package com.nemo.tmdb.view

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nemo.tmdb.R
import com.nemo.tmdb.model.ClickListener
import com.nemo.tmdb.model.Fav
import com.nemo.tmdb.model.FavDB
import com.nemo.tmdb.model.MoviesResponse
import com.nemo.tmdb.model.MoviesResponse.Movies
import com.nemo.tmdb.utils.Utils
import kotlinx.coroutines.*

class MoviesAdapter(itemClickedCb: ClickListener, starClickedCb: ClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG: String = "Adapter"

    companion object {
        private lateinit var mItemCallback: ClickListener
        private lateinit var mStarCallback: ClickListener
        private var mMovies = ArrayList<MoviesResponse.Movie>()
    }

    init {
        mItemCallback = itemClickedCb
        mStarCallback = starClickedCb
    }

    fun addMovies(resp: Movies?) {
        if (resp != null) {
            for (movie in resp.results!!) {
                mMovies.add(movie)
            }
        }
        notifyDataSetChanged()
    }

    fun clearMovies() {
        mMovies.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder.create(parent, mItemCallback, mStarCallback)
    }

    override fun getItemCount(): Int = mMovies.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = (holder as MovieViewHolder).itemView.context
        var image_url: String = Utils.getImgRoot() + mMovies[position]?.backdrop
        Log.d("Adapter", "Image url: "+image_url)
        (holder).mMoviesTitleTextView.text = mMovies[position].title
        Glide.with(context).asBitmap().load(image_url).apply(RequestOptions().centerCrop()).into((holder).mMoviesPosterImageView)
        (holder).mMoviesReleasedTextView.text = mMovies[position].release_date
        (holder).mMoviesRatingTextView.text = context.getString(R.string.item_rating) + mMovies[position].vote_av.toString()
        (holder).mMoviesPopularityTextView.text = context.getString(R.string.item_popularity) + mMovies[position].popularity.toString()
        (holder).mMovieStarImageView.setImageResource(R.drawable.ic_star_border_black_24dp)

        GlobalScope.launch(Dispatchers.IO) {
            var fav: Fav? = FavDB.getInstance(context)?.favDao()?.getFav(mMovies[position].id!!)
            if (fav != null) {
                MainScope().launch {
                    (holder).mMovieStarImageView.setImageResource(R.drawable.ic_star_black_24dp)
                }
            }
        }

        (holder).mMovieStarImageView.setOnClickListener { view ->
            Log.d("Adapter", "Clicked star on position "+position.toString())
            mStarCallback.onClick(view, mMovies[position].id!!)
            GlobalScope.launch(Dispatchers.IO) {
                var fav: Fav? = FavDB.getInstance(context)?.favDao()?.getFav(mMovies[position].id!!)
                if (fav != null) {
                    FavDB.getInstance(context)?.favDao()?.deleteFav(fav!!)
                    MainScope().launch {
                        (view as ImageView).setImageResource(R.drawable.ic_star_border_black_24dp)
                    }
                } else {
                    fav = Fav(mMovies[position].id!!, mMovies[position].title!!)
                    FavDB.getInstance(context)?.favDao()?.addFav(fav!!)
                    MainScope().launch {
                        (view as ImageView).setImageResource(R.drawable.ic_star_black_24dp)
                    }
                }
            }
        }

        (holder).mMovieItemLayout.setOnClickListener { view ->
            //onClickListener.invoke(view, category)
            Log.d("Adapter", "Clicked on item position "+position.toString())
            mItemCallback.onClick(view, mMovies[position].id!!)
        }

    }

    class MovieViewHolder(itemView: View, onItemClickListener: ClickListener, onStarClickListener: ClickListener) : RecyclerView.ViewHolder(itemView) {
        val mMoviesTitleTextView = itemView.findViewById<TextView>(R.id.movie_title)!!
        val mMoviesPosterImageView = itemView.findViewById<ImageView>(R.id.movie_poster)!!
        val mMoviesRatingTextView = itemView.findViewById<TextView>(R.id.movie_rating)!!
        val mMoviesReleasedTextView = itemView.findViewById<TextView>(R.id.release_date)!!
        val mMoviesPopularityTextView = itemView.findViewById<TextView>(R.id.popularity)!!
        val mMovieStarImageView = itemView.findViewById<ImageView>(R.id.favorite)!!
        val mMovieItemLayout = itemView.findViewById<View>(R.id.item_layout)!!

        companion object {
            fun create(parent: ViewGroup, itemClickedCb: ClickListener, starClickedCb: ClickListener): MovieViewHolder {
                return MovieViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_movie, parent, false), itemClickedCb, starClickedCb
                )
            }
        }
    }
}