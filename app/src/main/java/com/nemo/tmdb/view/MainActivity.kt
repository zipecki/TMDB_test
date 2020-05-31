package com.nemo.tmdb.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nemo.tmdb.R
import com.nemo.tmdb.model.MoviesResponse
import com.nemo.tmdb.network.ApiClient
import com.nemo.tmdb.utils.Utils
import com.nemo.tmdb.viewmodel.ActivityViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    var arrayAdapter: ArrayAdapter<String>? = null
    var mainViewModel : ActivityViewModel? = null;
    var moviesList : List<MoviesResponse.Movie>? = null
    private val mCompositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)
        mainViewModel?.retrieveAllFavsFromDb()

        arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        val listView = findViewById<View>(R.id.search_results) as ListView
        listView.adapter = arrayAdapter

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            Log.d("Activity", "Lista click, pos = "+position.toString())
            var movieSel : MoviesResponse.Movie =  moviesList!!.get(position)
            mainViewModel!!.setCurrMovie(movieSel)
            goToDetails()
        }

        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment, MoviesListFragment(), "movieList")
                .commit()

        RxTextView.textChanges(search_text)
                .doOnNext { text: CharSequence? -> clearSearchResults() }
                .filter { text: CharSequence -> text.length >= 3 }
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { text: CharSequence? -> updateSearchResults(text!!) }

        search_text.setVisibility(View.VISIBLE)
        search_results.setVisibility(View.GONE)

    }

    fun clearSearchResults() {
        arrayAdapter?.clear()
    }

    open fun updateSearchResults(text: CharSequence) {
        search_results.setVisibility(View.VISIBLE)

        mCompositeDisposable.add(ApiClient.create().searchMovies(Utils.getApiKey(), text.toString())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<MoviesResponse.Movies>() {
                    override fun onNext(t: MoviesResponse.Movies) {
                        moviesList = t.results
                        val list: MutableList<String> = ArrayList()
                        if (moviesList != null) {
                            for (movie in moviesList!!) {
                                list.add(movie.title!!)
                            }
                            arrayAdapter?.clear()
                            arrayAdapter?.addAll(list)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("MainActivity", e.message)
                    }

                    override fun onComplete() {
                        Log.d("MainActivity", "Search completed")
                    }
                }))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> goToDetails()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        val movieFrag: Movie2Fragment? = getSupportFragmentManager().findFragmentByTag("movieDetail") as Movie2Fragment?

        if (movieFrag != null && movieFrag.isVisible()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, MoviesListFragment(), "movieList")
                    .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                    .commit()
            search_text.setVisibility(View.VISIBLE)
            search_results.setVisibility(View.GONE)

        } else {
            super.onBackPressed()
        }
    }

    fun getViewModel() : ActivityViewModel {
        return mainViewModel!!
    }

    fun goToDetails() {
        search_text.setVisibility(View.GONE)
        search_results.setVisibility(View.GONE)
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, Movie2Fragment(), "movieDetail")
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                .commit()

        val trans: FragmentTransaction =  getSupportFragmentManager().beginTransaction()
        val listFrag: MoviesListFragment = getSupportFragmentManager().findFragmentByTag("movieList") as MoviesListFragment
        trans.remove(listFrag).commit()

        search_text.setVisibility(View.GONE)
        search_results.setVisibility(View.GONE)
        // hide keyboard
        val inputMethodManager = (this as Context).getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0)

    }
}
