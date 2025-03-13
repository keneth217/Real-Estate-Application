package com.keneth.realestateapplication.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keneth.realestateapplication.data.PropertyType
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import kotlinx.coroutines.launch
import java.util.UUID


@Composable
fun PropertyTypeScreen(
    navController: NavController,
    viewModel: PropertyViewModel
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    var propertyName by remember { mutableStateOf("") }
    var showAddStatusDialog by remember { mutableStateOf(false) }
    val myProperties = viewModel.propertyTypes.value

    var topBarTitle by remember { mutableStateOf("PropertyTypes Lists") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = topBarTitle,
                onMenuClick = {
                    navController.popBackStack()
                }
            )
        }, containerColor = Color.White, contentColor = Color.Black,

        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddStatusDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Status") },
                text = { Text("Add Status") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (myProperties.isEmpty()) {
                Text(
                    text = "No property Type found!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(myProperties) { property -> // Use myProperties (List<Property>)
                        PropertyItem(propertyType = property) // Pass each property to PropertyItem
                    }
                }
            }
        }
    }

    // Add Status Dialog
    if (showAddStatusDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddStatusDialog = false
                propertyName = ""  // Reset input field
            },
            title = { Text("Add New Status") },
            text = {
                Column {
                    OutlinedTextField(
                        value = propertyName,
                        onValueChange = { propertyName = it },
                        label = { Text("Property Type") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = UUID.randomUUID().toString()
                        val newProperty = PropertyType(uuid = id, name = propertyName)
                        viewModel.addPropertyType(newProperty)
                        propertyName = "" // Clear input
                        showAddStatusDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showAddStatusDialog = false
                        propertyName = "" // Reset input when dismissed
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
// Moved outside PropertyTypeScreen
@Composable
fun PropertyItem(propertyType: PropertyType) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = propertyType.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
