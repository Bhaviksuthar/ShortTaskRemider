package com.mew.shorttaskreminder.Screens

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service.NOTIFICATION_SERVICE
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.mew.shorttaskreminder.DB.Task
import com.mew.shorttaskreminder.R
import com.mew.shorttaskreminder.Receiver.TaskReminderReceiver
import com.mew.shorttaskreminder.Service.TaskService
import com.mew.shorttaskreminder.Viewmodel.TaskViewModelFactory
import com.mew.shorttaskreminder.Viewmodel.TaskViewmodel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddTaskScreen(viewmodel: TaskViewmodel,navController: NavController){
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val selectedTime = remember {
        mutableStateOf("")
    }

    val title = remember {
        mutableStateOf("")
    }
    val content = remember {
        mutableStateOf("")
    }

    var selectedTimeinmillis = remember {
        mutableStateOf("")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Your Task", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black))
                },
                colors =  TopAppBarColors(
                    containerColor = Color.Gray,
                    scrolledContainerColor = Color.White,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.White
                )
            )
        },

        content = { paddingValues ->  
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()){
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(10.dp,10.dp),
                    shape = RectangleShape,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    value = title.value,
                    textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.W600),
                    onValueChange = {
                        title.value = it
                    },
                    label = {
                        Text("Your Task title here...")
                    }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().height(200.dp).padding(10.dp,10.dp),
                    shape = RectangleShape,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.W200),
                    value = content.value,
                    onValueChange = {
                        content.value = it
                    },
                    label = {
                        Text("Your Task Content here...")
                    }
                )
                Column(modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    ElevatedButton(
                        modifier = Modifier.width(200.dp).height(45.dp),
                        colors = ButtonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White,
                            disabledContentColor = Color.Unspecified,
                            disabledContainerColor = Color.Unspecified
                        ),
                        onClick = {
                            openTimePicker(context,calendar,selectedTime,selectedTimeinmillis)
                        }
                    ) {
                        Text("Add Reminder", style = TextStyle(fontSize = 18.sp))
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    Text("${selectedTime.value}", style = TextStyle(fontSize = 24.sp))
                }
                Spacer(modifier = Modifier.size(20.dp))
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                    ElevatedButton(
                        modifier = Modifier.width(200.dp).height(55.dp),
                        colors = ButtonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.White,
                            disabledContentColor = Color.Unspecified,
                            disabledContainerColor = Color.Unspecified
                        ),
                        onClick = {
                            AddTaskAndSchedule(title.value,content.value,selectedTime.value,selectedTimeinmillis.value,
                                context,viewmodel,navController)
                        }
                    ) {
                        Text("Add Task Schedule", style = TextStyle(fontSize = 18.sp))
                    }
                }

            }
        }
    )
}

fun AddTaskAndSchedule(title: String, content: String, selectedTime: String,
                       selectedTimeMillis: String,context: Context,viewmodel: TaskViewmodel,navController: NavController) {
    if (selectedTime.isEmpty() && selectedTimeMillis.isEmpty()){
        Toast.makeText(context,"Please select time",Toast.LENGTH_SHORT).show()
    }
    else if (title.isEmpty()){
        Toast.makeText(context,"Please enter task title",Toast.LENGTH_SHORT).show()
    }
    else if (content.isEmpty()){
        Toast.makeText(context,"Please enter task content",Toast.LENGTH_SHORT).show()
    }
    else{
        var task = Task(title,content,selectedTimeMillis,selectedTime,false)
        viewmodel.add(task)
        scheduleReminder(context,task,navController)
    }
}

@SuppressLint("ScheduleExactAlarm")
fun scheduleReminder(context: Context, task: Task,navController: NavController){

    val intent = Intent(context, TaskService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
        createNotification(task,context)
    } else {
        context.startService(intent)
        createNotification(task,context)
    }

    navController.enableOnBackPressed(true)
    navController.popBackStack()
}

fun createNotification(task: Task,context: Context) {
    val channelID = "CHANNEL"
    val notification = NotificationCompat.Builder(context,channelID)
        .setContentTitle(task.taskTitle)
        .setSubText("${task.content} is scheduled")
        .setAutoCancel(false)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .build()

    val notificationChannel : NotificationChannel
    val notificationManager : NotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        notificationChannel = NotificationChannel(channelID,"Scheduled_Notification",
            NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    notificationManager.notify(1,notification)

}

fun openTimePicker(context : Context,calendar: Calendar,selectedTime : MutableState<String>, selectedTimeMillis: MutableState<String>) {
    val timePicker = TimePickerDialog(context,object : TimePickerDialog.OnTimeSetListener{
        override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, p1)
                set(Calendar.MINUTE, p2)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

            }
            selectedTimeMillis.value = calendar.timeInMillis.toString()
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            selectedTime.value = timeFormat.format(selectedCalendar.time)
        }

    },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false)

    timePicker.show()
}
