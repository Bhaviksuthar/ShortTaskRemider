package com.mew.shorttaskreminder.Screens

import android.app.Activity
import android.app.Service.VIBRATOR_SERVICE
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mew.shorttaskreminder.DB.Task
import com.mew.shorttaskreminder.Viewmodel.TaskViewmodel
import kotlin.system.exitProcess

@Composable
fun NewScreen(id : Int,title : String,status : Boolean,content : String
              ,timeml : String,time : String,viewmodel: TaskViewmodel){

    val context = LocalContext.current
    val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        Text(
           text =  "${title} Task Reminder",style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = Color.Red, textAlign = TextAlign.Center
        ))
        Spacer(modifier = Modifier.size(10.dp))
        ElevatedButton(
            onClick = {
                val task = Task()
                task.id = id
                task.taskTitle = title
                task.content = content
                task.time = timeml
                task.timeString = time
                task.status = true
                viewmodel.update(task)
                vibrator.cancel()
                Toast.makeText(context,"Task Completed",Toast.LENGTH_SHORT).show()
                (context as Activity).finish()
            }
        ) {
            Text("Complete", style = TextStyle(fontSize = 18.sp))
        }
    }
}