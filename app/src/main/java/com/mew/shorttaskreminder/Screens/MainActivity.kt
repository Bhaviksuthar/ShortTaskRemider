package com.mew.shorttaskreminder.Screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mew.shorttaskreminder.R
import com.mew.shorttaskreminder.Viewmodel.TaskViewModelFactory
import com.mew.shorttaskreminder.Viewmodel.TaskViewmodel
import com.mew.shorttaskreminder.ui.theme.ShortTaskReminderTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            if(checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),1000)
            }

            val viewmodel = ViewModelProvider(this, TaskViewModelFactory(this))[TaskViewmodel::class.java]

            val id = intent.getIntExtra("taskId",-1)
            val title = intent.getStringExtra("TaskName")
            val status = intent.getBooleanExtra("TaskStatus",false)
            val navigateToTaskDetail = intent.getBooleanExtra("navigateToTaskDetail",false)
            val content = intent.getStringExtra("Content")
            val timeml = intent.getStringExtra("TimeMl")
            val time = intent.getStringExtra("Time")

            if (navigateToTaskDetail){
                NewScreen(id,title!!,status,content!!,timeml!!,time!!,viewmodel)
            }
            else {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "SplashScreen") {

                    composable("SplashScreen") {
                        SplashScreen(navController)
                    }

                    composable("AllTasksScreen") {
                        AllTasksScreen(navController, viewmodel)
                    }
                    composable("AddTaskScreen") {
                        AddTaskScreen(viewmodel, navController)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000){
            val perm = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (perm){
                Toast.makeText(applicationContext,"Permission granted",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext,"Permission denied",Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable

fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShortTaskReminderTheme {
        Greeting("Android")
    }
}