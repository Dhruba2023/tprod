package com.digiwh.tprod.room

import androidx.room.*

@Dao
interface DefaultDao{

    @Query("SELECT * FROM TProd")
    fun allItems(): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Item)

    @Delete
    fun delete(item: Item)

    @Update
    fun update(item: Item)

    @Query("DELETE FROM TProd")
    fun nukeTable()

}