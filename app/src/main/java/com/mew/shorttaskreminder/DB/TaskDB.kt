package com.mew.shorttaskreminder.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 3, exportSchema = false)
abstract class TaskDB() : RoomDatabase(){
    abstract fun getDao() : TaskDao

    companion object{
        var INSTANCE : TaskDB? = null

        fun getInstance(context: Context):TaskDB{
            synchronized(this){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context,TaskDB::class.java,"Taskdb")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}