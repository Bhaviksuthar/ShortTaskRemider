package com.mew.shorttaskreminder.Viewmodel

import android.content.Context
import com.mew.shorttaskreminder.DB.Task
import com.mew.shorttaskreminder.DB.TaskDB
import com.mew.shorttaskreminder.DB.TaskDao

class TaskRepository(context: Context) {

    var taskDb = TaskDB.getInstance(context)
    var taskDao = taskDb.getDao()
    var taskData = taskDao.getAllTask()

    fun add(task: Task){
        taskDao.add(task)
    }

    fun update(task: Task){
        taskDao.update(task)
    }

    fun delete(id : Int){
        taskDao.delete(id)
    }

    fun getAllTask():List<Task>{
       return taskDao.getAllTask2()
    }
}