package com.nemo.tmdb.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.nemo.tmdb.model.ClickListener
import com.nemo.tmdb.model.MoviesResponse
import com.nemo.tmdb.network.ApiClient
import com.nemo.tmdb.utils.Utils
import com.nemo.tmdb.viewmodel.ActivityViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class MoviesListFragment : Fragment() {
    private val mCompositeDisposable = CompositeDisposable()
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: MoviesAdapter? = null
    var mainViewModel : ActivityViewModel? = null
    var itemClickListener : ItemClickListener? = null
    var starClickListener : StarClickListener? = null

    class ItemClickListener(ctx:Context): ClickListener(ctx) {
        override fun onClick(view: View, id: Int) {
            Log.d("ListView", "onItemClickListener called, movie id: " + id)

            if (ctx != null) {
                (ctx as MainActivity).getViewModel().setCurrMovie(id)
                (ctx as MainActivity).goToDetails()
            }
        }
    }

    class StarClickListener(ctx:Context): ClickListener(ctx) {
        override fun onClick(view: View, id: Int) {
            Log.d("ListView", "onItemClickListener called, movie id: " + id)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view :View = inflater.inflate(com.nemo.tmdb.R.layout.fragment_list, container, false)

        itemClickListener = ItemClickListener(context!!)
        starClickListener = StarClickListener(context!!)

        mRecyclerView = view.findViewById(com.nemo.tmdb.R.id.movies)
        setupRecyclerView()
        fetchingMovies(Utils.getApiKey())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProviders.of(activity!!).get(ActivityViewModel::class.java)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(getActivity())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView?.setHasFixedSize(true)
        mRecyclerView?.layoutManager = layoutManager
        mRecyclerView?.setOnClickListener {v -> Log.d("Fragment", "Clicked movies list")  }
        mAdapter = MoviesAdapter(itemClickListener as ClickListener, starClickListener as ClickListener)
        mRecyclerView?.adapter = mAdapter
    }

    private fun fetchingMovies(api_key: String) {
        mCompositeDisposable.add(ApiClient.create().getMovies(api_key)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<MoviesResponse.Movies>() {
                    override fun onNext(t: MoviesResponse.Movies) {
                        mAdapter?.clearMovies()
                        mAdapter?.addMovies(t)
                        mainViewModel?.addMovies(t)
                    }

                    override fun onError(e: Throwable) {
                        Log.d("MainActivity", e.message)
                    }

                    override fun onComplete() {
                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show()
                    }
                }))
    }
}