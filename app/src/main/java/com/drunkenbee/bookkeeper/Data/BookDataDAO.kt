package com.drunkenbee.bookkeeper.Data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BookDataDAO {

    @Query("SELECT * FROM book WHERE is_partial_delete == 0")
    fun getAll(): LiveData<List<BookData>>

    @Query("SELECT * FROM book WHERE is_partial_delete == 0")
    fun getAllList(): List<BookData>

    @Insert
    fun insert(books: BookData)

    @Delete
    fun delete(book: BookData)

    @Update
    fun update(book: BookData)

    @Query("UPDATE book SET is_partial_delete = :status WHERE bid == :id")
    fun updateStatus(id: Int, status : Int)


}