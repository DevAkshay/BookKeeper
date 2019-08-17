package com.drunkenbee.bookkeeper.Data

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class BookRepository(application: Application) {

    private var bookDataDoa: BookDataDAO

    private var allBooks: LiveData<List<BookData>>

    init {
        val database: AppDatabase = AppDatabase.getInstance(application.applicationContext)
        bookDataDoa = database.bookDataDAO()
        allBooks = bookDataDoa.getAll()
    }

    fun insert(bookData: BookData) {
        val insertBookAsynTask = InsertBookAsynTask(bookDataDoa).execute(bookData)
    }

    fun delete(bookData: BookData) {
        val deleteBookAsynTask = DeleteBookAsynTask(bookDataDoa).execute(bookData)
    }

    fun updateSatus(id: Int, status: Int){
        val updateStatusBookAsynTask = UpdateStatusBookAsynTask(bookDataDoa).execute(id, status)
    }

    fun update(bookData:BookData)
    {
        val updateBookAsynTask = UpdateBookAsynTask(bookDataDoa).execute(bookData)
    }

    fun getAllNotes() : LiveData<List<BookData>>{
        return allBooks
    }

    private class InsertBookAsynTask(bookdataDAO: BookDataDAO) : AsyncTask<BookData, Unit, Unit>()
    {
        val bookDataDAO = bookdataDAO
        override fun doInBackground(vararg params: BookData?) {
            params[0]?.let { bookDataDAO.insert(it) }
        }
    }

    private class DeleteBookAsynTask(bookdataDAO: BookDataDAO) : AsyncTask<BookData, Unit, Unit>()
    {
        val bookDataDAO = bookdataDAO
        override fun doInBackground(vararg params: BookData?) {
            params[0]?.let { bookDataDAO.delete(it) }
        }

    }

    private class UpdateBookAsynTask(bookdataDAO: BookDataDAO) : AsyncTask<BookData, Unit, Unit>()
    {
        val bookDataDAO = bookdataDAO
        override fun doInBackground(vararg params: BookData?) {
            params[0]?.let { bookDataDAO.update(it) }
        }

    }

    private class UpdateStatusBookAsynTask(bookdataDAO: BookDataDAO) : AsyncTask<Int, Unit, Unit>()
    {
        val bookDataDAO = bookdataDAO
        override fun doInBackground(vararg params: Int?) {
            bookDataDAO.updateStatus(params[0]!!, params[1]!!) }
    }
}