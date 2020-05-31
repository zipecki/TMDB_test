package com.nemo.tmdb.model

import android.arch.persistence.room.*

@Dao
interface FavDao {

    @Query("SELECT * FROM favorities")
    fun getAllFavs(): List<Fav>?

    @Query("SELECT * FROM favorities WHERE id = :movieId")
    fun getFav(movieId: Int): Fav?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFav(fav: Fav)

    @Delete
    fun deleteFav(fav: Fav)

}