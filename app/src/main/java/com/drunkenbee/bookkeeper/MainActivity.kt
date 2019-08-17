package com.drunkenbee.bookkeeper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.drunkenbee.bookkeeper.Adapter.RecyclerAdapter
import com.drunkenbee.bookkeeper.Data.BookData
import com.drunkenbee.bookkeeper.Notification.NotifyWorker
import com.drunkenbee.bookkeeper.ViewModel.BookViewModel
import com.drunkenbee.bookkeeper.ViewModel.BookViewModelFactory
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.vivekkaushik.datepicker.DatePickerTimeline
import com.vivekkaushik.datepicker.OnDateSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter : RecyclerAdapter
    private var bookList : ArrayList<BookData> = arrayListOf()
    private lateinit var bookViewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bookViewModel = ViewModelProviders.of(this, BookViewModelFactory(this.application)).get(BookViewModel::class.java)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        bookViewModel.getAllBooks().observe(this, androidx.lifecycle.Observer<List<BookData>> {
            if(it.count() == 0)
                no_book_layout.visibility = View.VISIBLE
            else
                no_book_layout.visibility = View.GONE

            resetList(it)
            if(adapter!=null)
                adapter.notifyDataSetChanged()
        })


        adapter = RecyclerAdapter(
            bookList,
            object : RecyclerAdapter.IAdapterHandler {
                override fun updateItem(book: BookData?) {
                    showPopup(window.decorView.rootView, true, book)
                }

                override fun removeItem(pos: Int, book: BookData?) {
                    try {
                        bookList.removeAt(pos)
                        if (book != null) {
                            bookViewModel.updateStatus(book.bid, 1)
                        }
                        adapter.notifyItemRemoved(pos)
                    } catch (ex: IndexOutOfBoundsException) {
                        Log.e("QUICK DELETE OF ITEM", "Index out of bound")
                    }
                }

                override fun showUndoSnack(pos: Int, book: BookData?) {
                    var isUndo = false;
                    val snackbar = Snackbar.make(
                        mainLinearLayout, resources.getText(R.string.book_deleted),
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.view.setBackgroundColor(resources.getColor(R.color.colorBlue))
                    snackbar.setAction("Undo") {
                        if(!bookList.contains(book)) {
                            addBookData(pos, book)
                            if (book != null) {
                                bookViewModel.updateStatus(book.bid, 0)
                            }
                            isUndo = true
                        }
                    }
                    snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {

                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            if (book != null && !isUndo) {
                                bookViewModel.delete(book)
                            }
                        }
                    })
                    snackbar.show()
                }
            })

        recyclerView.adapter = adapter

        button.setOnClickListener{showPopup(it, false)}

    }

    fun addBookData(pos: Int, book: BookData?)
    {
        if (book != null) {
            bookList.add(pos,book)
        };
        adapter.notifyItemInserted(pos)
    }

    fun showPopup(v:View, isEdit: Boolean, book: BookData? = null)
    {
        val layoutInflater = baseContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = layoutInflater.inflate(R.layout.popup_add_book, null)
        val popupWindow = PopupWindow(
            popupView,LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )

        val nameEditText = popupView.findViewById(R.id.editText) as EditText
        val btnAdd = popupView.findViewById(R.id.add) as Button
        val btnDismiss = popupView.findViewById(R.id.dismiss) as Button
        val datePickerTimeline = popupView.findViewById(R.id.datePickerTimeline) as DatePickerTimeline
        val bookNameErrorText  = popupView.findViewById(R.id.nameErrorMessage) as TextView
        val dateErrorText = popupView.findViewById(R.id.dateErrorMessage) as TextView

        bookNameErrorText.visibility = View.INVISIBLE
        dateErrorText.visibility = View.INVISIBLE

        if(isEdit && book != null)
            nameEditText.setText(book.bookName)

        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        datePickerTimeline.setInitialDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE))
        datePickerTimeline.setOnDateSelectedListener(object : OnDateSelectedListener {
            override fun onDateSelected(year: Int, month: Int, day: Int, dayOfWeek: Int) {
                dueDate.set(year, month, day)
            }

            override fun onDisabledDateSelected(year: Int, month: Int, day: Int, dayOfWeek: Int, isDisabled: Boolean) {
                dueDate.clear()

            }
        })
        // Disable date
        val dates = arrayOf<Date>(Calendar.getInstance().getTime())
        datePickerTimeline.deactivateDates(dates)

        btnDismiss.setOnClickListener{
            popupWindow.dismiss()
        }

        btnAdd.setOnClickListener {
            val name = nameEditText.text.toString()
            when {
                name.isEmpty() && dueDate.get(Calendar.DATE) == currentDate.get(Calendar.DATE) -> {
                    bookNameErrorText.visibility = View.VISIBLE
                    dateErrorText.visibility = View.VISIBLE
                }

                name.isEmpty() ->{
                    bookNameErrorText.visibility = View.VISIBLE
                    dateErrorText.visibility = View.INVISIBLE
                }

                dueDate.get(Calendar.DATE) == currentDate.get(Calendar.DATE) ->{
                    dateErrorText.visibility = View.VISIBLE
                    bookNameErrorText.visibility = View.INVISIBLE
                }

                else -> {

                    if(isEdit && book!=null)
                        bookViewModel.update(BookData(book.bid, nameEditText.text.toString(), dueDate.get(Calendar.DATE), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.YEAR), 0))
                    else
                        bookViewModel.insert(BookData(0, nameEditText.text.toString(), dueDate.get(Calendar.DATE), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.YEAR), 0))
                    popupWindow.dismiss()
                }
            }
        }

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.argb(170, 0, 0, 0)))
        popupWindow.showAtLocation(button, Gravity.CENTER, 0, 0)
        //popupWindow.showAsDropDown(button, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    fun resetList(list: List<BookData>) {
        bookList.clear()
        bookList.addAll(list)
    }

    @SuppressLint("RestrictedApi")
    fun setNotificationWorker()
    {
        val notifyWorkRequest = PeriodicWorkRequestBuilder<NotifyWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("Notification",ExistingPeriodicWorkPolicy.KEEP,notifyWorkRequest)
    }


    override fun onPause() {
        super.onPause()
        setNotificationWorker()
    }

}


