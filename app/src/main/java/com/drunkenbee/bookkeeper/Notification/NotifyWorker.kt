package com.drunkenbee.bookkeeper.Notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.drunkenbee.bookkeeper.Data.AppDatabase
import com.drunkenbee.bookkeeper.Data.BookData
import com.drunkenbee.bookkeeper.R
import java.util.*


class NotifyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Do the work here--in this case, upload the images.

        val books = AppDatabase.getInstance(this.applicationContext).bookDataDAO().getAllList()

        for(book in books)
        {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            book.year?.let { book.month?.let { it1 -> book.date?.let { it2 -> dueDate.set(it, it1, it2) } } }
            val remainingDays = (dueDate.time.time - currentDate.time.time) / (1000 * 60 * 60 * 24)
            if(remainingDays < 5 ) {
                triggerNotification(book)
            }

        }
        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }

    private fun triggerNotification(book: BookData){

        createChannel()
        val mNotification: Notification
        val context = this.applicationContext
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val title = this.applicationContext.getString(R.string.notification_message)
        val message = "Book: "+book.bookName+"     Due Date: "+book.date+"/"+book.month+"/"+book.year
        //val res = this.resources

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            mNotification = Notification.Builder(context, CHANNEL_ID)
                // Set the intent that will fire when the user taps the notification
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setStyle(Notification.BigTextStyle()
                    .bigText(message))
                .setContentText(message).build()
        } else {

            mNotification = Notification.Builder(context)
                // Set the intent that will fire when the user taps the notification
                .setSmallIcon(R.drawable.navigation_empty_icon)
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText(message))
                .setContentText(message).build()

        }



        // mNotificationId is a unique int for each notification that you must define
        notificationManager.notify(book.bid, mNotification)

    }

    private fun createChannel() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            val context = this.applicationContext
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = this.applicationContext.getString(R.string.notification_message)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {

        const val CHANNEL_ID = "com.drunkenbee.bookkeeper.CHANNEL_ID"
        const val CHANNEL_NAME = "Book Reminder"
    }

}