package com.mew.shorttaskreminder.Screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.shapes.Shape
import android.widget.ImageButton
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.mew.shorttaskreminder.DB.Task
import com.mew.shorttaskreminder.R
import com.mew.shorttaskreminder.Viewmodel.TaskViewmodel
import kotlin.math.tan

@Composable
@SuppressLint("ResourceAsColor")
@OptIn(ExperimentalMaterial3Api::class)
fun AllTasksScreen(navController: NavController,viewmodel: TaskViewmodel){

    val taskList by viewmodel.getAllTask.observeAsState(initial = emptyList())
    val context = LocalContext.current

    var searchQuery = remember {
        mutableStateOf("")
    }

    val filteredItems = taskList.filter {
        var stat : String = ""
        it.taskTitle.contains(searchQuery.value,ignoreCase = true)
                || (if (it.status) "Completed".contains(searchQuery.value,ignoreCase = true) else "Active".contains(searchQuery.value,ignoreCase = true))


    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("AddTaskScreen")
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = "", modifier = Modifier.width(35.dp).height(35.dp))
            }
        },

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "All Tasks",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarColors(
                    containerColor = Color.Gray,
                    scrolledContainerColor = Color.White,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.White
                )
            )
        },

        content = { padding->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CircleShape,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        trailingIcon = {
                            Icon(Icons.Default.Search,"")
                        },
                        value = searchQuery.value,
                        onValueChange = {
                            searchQuery.value = it
                        },
                        label = {
                            Text("Search here....")
                        }
                    )
                }
                if (taskList.isNotEmpty()){
                    LazyColumn() {

                        itemsIndexed(
                            items = filteredItems,
                            itemContent = {index,item->
                                ItemCard(index,item,viewmodel, context)
                            }
                        )
                    }
                }
                else{
                    Column(modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {

                        Text("No Task Found", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
                    }
                }

            }
        }
    )
}

@Composable
fun ItemCard(index: Int, item: Task,viewmodel: TaskViewmodel,context: Context) {

    val showmenu = remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,

        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(15.dp,10.dp)
            ) {
                Icon(modifier = Modifier.align(Alignment.TopEnd).size(25.dp).clickable {
                    showmenu.value = true
                }
                    ,painter = painterResource(R.drawable.dots), contentDescription = "")

                Row(modifier = Modifier.align(Alignment.TopEnd)) {
                    DropdownMenu(
//                        offset = DpOffset(x = (-30).dp, y = (-80).dp),
                        expanded = showmenu.value,
                        onDismissRequest = { showmenu.value = false }

                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("Complete Task")
                            },
                            onClick = {
                                if (!item.status){
                                    val task = Task()
                                    task.id = item.id
                                    task.taskTitle = item.taskTitle
                                    task.content = item.content
                                    task.status = true
                                    task.time = item.time
                                    task.timeString = item.timeString
                                    viewmodel.update(task)
                                    showmenu.value = false
                                }
                                else{
                                    Toast.makeText(context,"Already Completed",Toast.LENGTH_SHORT).show()
                                    showmenu.value = false
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("Delete")
                            },
                            onClick = {
                                viewmodel.delete(item.id!!)
                                showmenu.value = false
                            }
                        )
                    }
                }


                Column {
                    Text("${item.taskTitle}", style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = "${item.content}", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal))
                    Spacer(modifier = Modifier.size(10.dp))
                    if (item.status){
                        Text("Status : Completed", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal))
                    }
                    else{
                        Text("Status : Active", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal))
                    }
                }

                Spacer(modifier = Modifier.size(5.dp))
            }
        }
    }

}

@Composable
fun CreatePopupmenu(item: Task, index: Int,viewmodel: TaskViewmodel,context: Context) {

}
