package com.drunkenbee.bookkeeper.Data

import android.content.Context
import androidx.room.*

@Database(entities = arrayOf(BookData::class), version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDataDAO() : BookDataDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "book_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}