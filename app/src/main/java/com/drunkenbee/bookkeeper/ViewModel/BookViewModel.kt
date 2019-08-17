package com.drunkenbee.bookkeeper.ViewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.drunkenbee.bookkeeper.Data.BookData
import com.drunkenbee.bookkeeper.Data.BookRepository

class BookViewModel internal  constructor(application: Application) : ViewModel() {

    private var repository: BookRepository = BookRepository(application)
    private var allBooks : LiveData<List<BookData>> = repository.getAllNotes()

    fun insert(bookData: BookData) {
        repository.insert(bookData)
    }

    fun delete(bookData: BookData){
        repository.delete(bookData)
    }

    fun update(bookData: BookData){
        repository.update(bookData)
    }

    fun getAllBooks() : LiveData<List<BookData>>{
        return allBooks
    }

    fun updateStatus(id: Int, status: Int)
    {
        repository.updateSatus(id,status)
    }
}