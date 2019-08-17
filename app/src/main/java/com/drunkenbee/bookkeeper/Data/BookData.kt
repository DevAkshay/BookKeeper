package com.drunkenbee.bookkeeper.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class BookData(
    @PrimaryKey(autoGenerate = true) val bid: Int,
    @ColumnInfo(name = "book_name") val bookName: String?,
    @ColumnInfo(name = "date") val date: Int?,
    @ColumnInfo(name = "month") val month: Int?,
    @ColumnInfo(name = "year") val year: Int?,
    @ColumnInfo(name = "is_partial_delete") val isPartialDelete: Int
)