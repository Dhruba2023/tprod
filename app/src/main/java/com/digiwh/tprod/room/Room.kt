package com.digiwh.tprod.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1)
abstract class Databases: RoomDatabase(){
    abstract val defaultDao: DefaultDao
}