package com.nemo.tmdb.model

import android.content.Context
import android.view.View

open class ClickListener(var ct: Context) {

    var ctx: Context? = null
    init {
        ctx = ct
    }

    open fun onClick(view: View, id: Int) {

    }

}