package com.mew.shorttaskreminder.Service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.mew.shorttaskreminder.DB.Task
import com.mew.shorttaskreminder.R
import com.mew.shorttaskreminder.Screens.MainActivity
import com.mew.shorttaskreminder.Viewmodel.TaskRepository
import com.mew.shorttaskreminder.Viewmodel.TaskViewModelFactory
import com.mew.shorttaskreminder.Viewmodel.TaskViewmodel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class TaskService : Service(){

    private val handler = Handler(Looper.getMainLooper())
    lateinit var viewmodel: TaskViewmodel
    lateinit var vibrator: Vibrator

    override fun onCreate() {
        super.onCreate()
        viewmodel = TaskViewmodel(this)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        startForegroundService()
        startTimer()
    }


    fun startForegroundService(){
        Log.d("SERVICE__","Started")

        val channelID = "CHANNEL3"
        val notification = NotificationCompat.Builder(this,channelID)
            .setContentTitle("Started Task timer")
            .setSubText("Task Scheduled")
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        val notificationChannel : NotificationChannel
        val notificationManager : NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            notificationChannel = NotificationChannel(channelID,"Scheduled_Notification3",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(1,notification)
        startForeground(1,notification)
    }

    fun startTimer(){
        handler.postDelayed(object : Runnable {
            override fun run() {
                Log.d("SERVICE___","Running")
                checkAndNotifyTasks()
                handler.postDelayed(this, 1000) // Schedule next check
            }
        }, 1000)
    }

    // Method to check if any task is due and show a notification
    private fun checkAndNotifyTasks() {
        val taskList = viewmodel.getAllTask2()
        val currentTime = System.currentTimeMillis()

        for (task in taskList) {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val crtime = timeFormat.format(currentTime)

            if(crtime == task.timeString && !task.status){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    sendNotification(task)
                } else {
                    vibrator.vibrate(1000)
                    sendNotification(task)
                }
                Log.d("SERVICE___","currenttime = $crtime / giventime = ${task.timeString}")
            }
            else{
                Log.d("SERVICE___","currenttime2 = $crtime / giventime2 = ${task.timeString}")
            }
        }
    }


    private fun sendNotification(task: Task) {

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigateToTaskDetail", true)
            putExtra("taskId", task.id)
            putExtra("TaskName",task.taskTitle)
            putExtra("TaskStatus",task.status)
            putExtra("Content",task.content)
            putExtra("TimeMl",task.time)
            putExtra("Time",task.timeString)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelID = "CHANNEL2"
        val notification = NotificationCompat.Builder(this,channelID)
            .setContentTitle("Time to complete : ${task.taskTitle}")
            .setSubText("Task Scheduled comes")
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        val notificationChannel : NotificationChannel
        val notificationManager : NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            notificationChannel = NotificationChannel(channelID,"Scheduled_Notification2",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(1,notification)
    }

    private fun isTaskDue(task: Task, currentime: Long): Boolean {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val crtime = timeFormat.format(currentime)

        if(crtime == task.timeString && !task.status){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(1000) // Vibrate for 500 milliseconds
            }
            Log.d("SERVICE___","currenttime = $crtime / giventime = ${task.timeString}")
            return true
        }
        else{
            Log.d("SERVICE___","currenttime2 = $crtime / giventime2 = ${task.timeString}")
            return false
        }
    }

    private fun markTaskAsCompleted(task: Task) {
        task.status = true
        viewmodel.update(task)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel = TaskViewmodel(this)
        if (viewmodel.getAllTask2().all { it.status }){
            Log.d("SERVICE___","Completed")
            handler.removeCallbacksAndMessages(null)
        }
        else{
            Log.d("SERVICE___","Running2")
        }

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}