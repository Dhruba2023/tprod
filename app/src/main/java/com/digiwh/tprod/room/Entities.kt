package com.digiwh.tprod.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TProd")
data class Item constructor(
    @PrimaryKey
    @ColumnInfo(name = "Bag No") val bagNo: Long,
    @ColumnInfo(name = "Trough No") val troughNo: Long,
    @ColumnInfo(name = "KGs") val kgs: Double,
    @ColumnInfo(name = "Date") val date: String,
)