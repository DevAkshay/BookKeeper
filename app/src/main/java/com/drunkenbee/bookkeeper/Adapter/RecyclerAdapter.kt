package com.drunkenbee.bookkeeper.Adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drunkenbee.bookkeeper.Data.BookData
import com.drunkenbee.bookkeeper.inflate
import kotlinx.android.synthetic.main.recycleview_item_row.view.*
import java.util.*
import kotlin.collections.ArrayList
import android.text.Html
import android.os.Build


class RecyclerAdapter(private val books : ArrayList<BookData>, private val adapterHandler : IAdapterHandler) : RecyclerView.Adapter<RecyclerAdapter.BookHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val inflatedView = parent.inflate(com.drunkenbee.bookkeeper.R.layout.recycleview_item_row, false)
        return BookHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {

        holder.bindData(books[position], adapterHandler)
    }

    interface  IAdapterHandler
    {
        fun removeItem(pos: Int, book:BookData?)
        fun showUndoSnack(pos:Int, book: BookData?)
        fun updateItem(book: BookData?)
    }

    class BookHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var book: BookData? = null
        private lateinit var adapterHandler: IAdapterHandler

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
        }

        fun bindData(book : BookData, adapterHandler: IAdapterHandler){

            this.book = book
            this.adapterHandler = adapterHandler

            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            book.year?.let { book.month?.let { it1 -> book.date?.let { it2 -> dueDate.set(it, it1, it2) } } }
            view.textView.text = book.bookName
            val remainingDays = (dueDate.time.time - currentDate.time.time) / (1000 * 60 * 60 * 24)

            if(remainingDays > 0) {
                val text = "<font color=#7a7f91>Days Remaining </font>"
                if (Build.VERSION.SDK_INT >= 24)
                    view.textView2.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
                else
                    view.textView2.text = Html.fromHtml(text)
                view.textView2.append(remainingDays.toString())

            }else{
                val text = "<font color=#e02247>Renew or Return this Book</font>"
                if (Build.VERSION.SDK_INT >= 24)
                    view.textView2.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
                else
                    view.textView2.text = Html.fromHtml(text)
            }

            view.delete.setOnClickListener { onDeleteItem() }
            view.edit.setOnClickListener { onEditItem() }

        }

        fun onDeleteItem(){
            adapterHandler.removeItem(layoutPosition, book)
            adapterHandler.showUndoSnack(layoutPosition, book)
        }

        fun onEditItem(){
            adapterHandler.updateItem(book)
        }


    }

}