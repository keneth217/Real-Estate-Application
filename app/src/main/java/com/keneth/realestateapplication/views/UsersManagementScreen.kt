package com.keneth.realestateapplication.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keneth.realestateapplication.data.User
import com.keneth.realestateapplication.viewModels.PropertyViewModel
import com.keneth.realestateapplication.viewModels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun UsersManagementScreen(
    navController: NavController,
    viewModelUser: UserViewModel,
) {
    val userList = viewModelUser.usersLists.value

    LaunchedEffect(Unit) {
        viewModelUser.fetchAllUsers()
    }

    Log.d("UsersManagementScreen", "users lists ${userList.size}")

    Scaffold(
        topBar = {
            Screen.UsersManagementScreen.title?.let {
                AppTopBar(
                    title = it,
                    onMenuClick = { navController.popBackStack() }
                )
            }
        },
        containerColor = Color.White, contentColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .padding(8.dp)
                ) {
                    Text(text = "ID", modifier = Modifier.weight(1f))
                    Text(text = "Name", modifier = Modifier.weight(2f))
                    Text(text = "Email", modifier = Modifier.weight(3f))
                }
            }
            // Use itemsIndexed to get both index and user
            itemsIndexed(userList) { index, user ->
                SingleUser(
                    user = user,
                    index = index + 1,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun SingleUser(
    user: User,
    index: Int,
    navController: NavController
) {

    val backgroundColor = if (index % 2 == 0) Color.Gray else Color.LightGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable {
                val route = Screen.UserDetails.createRoute(user.uuid)
                navController.navigate(route)

                Log.d("Navigation", "Navigating to $route")
                Log.d("Navigation", "Property Id ${user.uuid}")
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the index instead of the UUID
        Text(text = "$index", modifier = Modifier.weight(1f))
        Text(text = user.firstName, modifier = Modifier.weight(2f))
        Text(text = user.email, modifier = Modifier.weight(3f))
    }
}
