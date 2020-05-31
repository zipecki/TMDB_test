package com.nemo.tmdb.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorities")
data class Fav(
    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,
    val title: String)

