package com.mew.shorttaskreminder.Viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mew.shorttaskreminder.DB.Task
import com.mew.shorttaskreminder.DB.TaskDB

class TaskViewmodel(context: Context) : ViewModel(){

    private var repository = TaskRepository(context)
    val getAllTask : LiveData<List<Task>> get() = repository.taskData

    fun add(task: Task){
        repository.add(task)
    }

    fun update(task: Task){
        repository.update(task)
    }

    fun delete(id : Int){
        repository.delete(id)
    }

    fun getAllTask2():List<Task>{
        return repository.getAllTask()
    }
}