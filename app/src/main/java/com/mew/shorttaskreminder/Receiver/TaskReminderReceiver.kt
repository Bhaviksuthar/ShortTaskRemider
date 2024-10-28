package com.mew.shorttaskreminder.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mew.shorttaskreminder.Service.TaskService

class TaskReminderReceiver() : BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        val taskId = p1!!.getIntExtra("TaskID",-1)
        val taskTitle = p1.getStringExtra("TaskTitle")

        val intent2 = Intent(p0,TaskService::class.java).apply {
            putExtra("TaskID",taskId)
            putExtra("TaskTitle",taskTitle)
        }

        p0!!.startForegroundService(intent2)

    }
}