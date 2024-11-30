package com.example.papb.pages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.PAPB.R
import com.example.papb.DatabaseViewModel
import com.example.papb.ui.theme.PAPBTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(databaseViewModel: DatabaseViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    var detail by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var editIndex by remember { mutableStateOf(-1) }
    var dataList by remember { mutableStateOf(listOf<Map<String, String>>()) }

    LaunchedEffect(Unit) {
        databaseViewModel.fetchData { fetchedData ->
            dataList = fetchedData
        }
    }

    PAPBTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logoputih),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(40.dp) // Mengatur ukuran gambar menjadi 40x40 dp
                                    .padding(end = 8.dp) // Memberi jarak antara gambar dan teks
                            )
                            Text(
                                text = "Home",
                                fontSize = 28.sp, // Ukuran font teks "Home" diatur menjadi 28 sp
                                style = MaterialTheme.typography.titleLarge // Menggunakan gaya Title
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)){
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(if (editIndex == -1) "Add Detail" else "Edit Detail", style = MaterialTheme.typography.bodyLarge) },
                        text = {
                            Column {
                                TextField(
                                    value = detail,
                                    onValueChange = { detail = it },
                                    label = { Text("Detail", style = MaterialTheme.typography.labelSmall) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("Description", style = MaterialTheme.typography.labelSmall) },

                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (editIndex == -1) {
                                    databaseViewModel.addData(
                                        detail,
                                        description,
                                        "INCOMPLETE"
                                    ) { success ->
                                        if (success) {
                                            databaseViewModel.fetchData { dataList = it }
                                        }
                                    }
                                } else {
                                    val documentId = dataList[editIndex]["id"] ?: ""
                                    val updatedData = mapOf(
                                        "detail" to detail,
                                        "description" to description,
                                        "status" to (dataList[editIndex]["status"] ?: "INCOMPLETE")
                                    )
                                    databaseViewModel.updateData(
                                        documentId,
                                        updatedData
                                    ) { success ->
                                        if (success) {
                                            databaseViewModel.fetchData { dataList = it }
                                        }
                                    }
                                }
                                showDialog = false
                                detail = ""
                                description = ""
                                editIndex = -1
                            }) { Text(if (editIndex == -1) "Add" else "Save", style = MaterialTheme.typography.bodyLarge) }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) { Text("Cancel", style = MaterialTheme.typography.bodyLarge) }
                        }
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    itemsIndexed(dataList) { index, item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "${item["detail"]}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "${item["description"]}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = "${item["status"]}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (item["status"] == "INCOMPLETE") MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.tertiary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = {
                                            editIndex = index
                                            detail = item["detail"] ?: ""
                                            description = item["description"] ?: ""
                                            showDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Text("Edit", style = MaterialTheme.typography.bodyLarge)
                                    }

                                    Button(
                                        onClick = {
                                            val documentId = item["id"] ?: ""
                                            if (documentId.isNotEmpty()) {
                                                databaseViewModel.deleteData(documentId) { success ->
                                                    if (success) {
                                                        databaseViewModel.fetchData { fetchedData ->
                                                            dataList = fetchedData
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Text("Delete", style = MaterialTheme.typography.bodyLarge)
                                    }

                                    Button(
                                        onClick = {
                                            val documentId = item["id"] ?: ""
                                            if (documentId.isNotEmpty()) {
                                                val updatedStatus =
                                                    if (item["status"] == "INCOMPLETE") "COMPLETED" else "INCOMPLETE"
                                                val updatedData = mapOf(
                                                    "detail" to (item["detail"] ?: ""),
                                                    "description" to (item["description"] ?: ""),
                                                    "status" to updatedStatus
                                                )
                                                databaseViewModel.updateData(
                                                    documentId,
                                                    updatedData
                                                ) { success ->
                                                    if (success) {
                                                        databaseViewModel.fetchData { fetchedData ->
                                                            dataList = fetchedData
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Text("Toggle Status", style = MaterialTheme.typography.bodyLarge)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}