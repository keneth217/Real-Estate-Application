package com.keneth.realestateapplication.views


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.keneth.realestateapplication.R

@Composable
fun ProfilePicture(
    profilePicture: String,
    profileImage: String,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Box(
        modifier = modifier

            .size(30.dp)
            .clip(CircleShape)
            .clickable { navController.navigate(Screen.Profile.route) }
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (profilePicture != null) {
            // Display local image from URI
            Image(
                painter = rememberAsyncImagePainter(model = profilePicture),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (profileImage.isNotEmpty()) {
            // Display remote image from URL
            SubcomposeAsyncImage(
                model = profileImage,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                error = {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Error Loading Image",
                        modifier = Modifier.size(50.dp)
                    )
                }
            )
        } else {

            Image(

                painter = painterResource(id = R.drawable.person),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }
}