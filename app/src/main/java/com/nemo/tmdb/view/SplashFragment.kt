package com.nemo.tmdb.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("Movie", "Call onCreateView in Splash fragment")
        val view: View = inflater.inflate(com.nemo.tmdb.R.layout.fragment_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Movie", "Call onViewCreated in Movie fragment")

        tmdb_title?.text = "TMDB - movies database"
        tmdb_address?.text = "www.themoviedb.org"
    }
}