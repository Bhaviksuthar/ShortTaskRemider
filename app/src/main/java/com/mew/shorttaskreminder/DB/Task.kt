package com.mew.shorttaskreminder.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "TaskTable")
class Task {
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null

    @ColumnInfo(name = "TaskTitle")
    var taskTitle : String = ""

    @ColumnInfo(name = "TaskContent")
    var content : String = ""

    @ColumnInfo(name = "Time")
    var time : String = ""

    @ColumnInfo(name = "TimeinString")
    var timeString : String = ""


    @ColumnInfo(name = "Status")
    var status : Boolean = false

    constructor(taskTitle: String, content: String,time : String,timeString : String, status: Boolean) {
        this.taskTitle = taskTitle
        this.content = content
        this.time = time
        this.timeString = timeString
        this.status = status
    }

    @Ignore
    constructor(){}
}