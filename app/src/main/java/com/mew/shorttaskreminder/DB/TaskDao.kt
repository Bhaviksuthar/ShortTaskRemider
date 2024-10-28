package com.mew.shorttaskreminder.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(task: Task)

    @Update
    fun update(task: Task)

    @Query("DELETE FROM TaskTable WHERE id=:id")
    fun delete(id : Int)

    @Query("SELECT * FROM TaskTable")
    fun getAllTask() : LiveData<List<Task>>

    @Query("SELECT * FROM TaskTable")
    fun getAllTask2() : List<Task>
}