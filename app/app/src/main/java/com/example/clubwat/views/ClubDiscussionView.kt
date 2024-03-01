package com.example.clubwat.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.clubwat.R
import com.example.clubwat.ui.theme.LightOrange
import com.example.clubwat.ui.theme.PurpleGrey80
import com.example.clubwat.viewmodels.ClubDiscussionViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDiscussionView(
    viewModel: ClubDiscussionViewModel,
    navController: NavController,
    clubId: String?
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        clubId?.let { clubId ->
            viewModel.fetchClubDetails(clubId)
            viewModel.fetchUpdatedPosts(clubId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon =
                {
                    IconButton(
                        onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.value.clubDetails?.title ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ) {
                itemsIndexed(uiState.value.posts) { index, post ->
                    val name =

                        if (uiState.value.posts.getOrNull(index - 1)?.messageData?.user?.email == post.messageData.user.email || post.isMe)
                            null
                        else post.messageData.user.first_name
                    MessageBubble(
                        isMe = post.isMe,
                        name = name,
                        message = post.messageData.message
                    )
                }
            }
            var text by remember { mutableStateOf("") }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                )
                Button(
                    onClick = {
                        if (text.isNotEmpty()) {
                            viewModel.sendMessage(
                                text,
                                uiState.value.clubDetails?.id,
                                uiState.value.posts.find { it.isMe }?.messageData?.user_id // WILL NOT WORK UNLESS THE GET REQUEST FINDS A MESSAGE FROM CURRENT USER
                            )
                        }
                        text = ""
                    },
                    modifier = Modifier.padding(end = 5.dp, top = 5.dp, bottom = 5.dp)
                ) {
                    // todo icon
                }
            }
        }
    }
}

@Composable
fun MessageBubble(isMe: Boolean, name: String?, message: String) {
    Column(
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        name?.let { name ->
            Text(
                text = name,
                modifier = Modifier.background(
                    color = Color.Transparent
                ),
                fontSize = 12.sp,
            )
        }
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (isMe) 48f else 0f,
                        bottomEnd = if (isMe) 0f else 48f
                    )
                )
                .background(if (isMe) LightOrange else PurpleGrey80)
                .padding(16.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.background(
                    color = Color.Transparent
                ),
                fontSize = 15.sp,
            )
        }
    }
}

@Preview
@Composable
fun ClubDiscussionViewPreview() {
    ClubDiscussionView(
        viewModel = viewModel(),
        navController = rememberNavController(),
        clubId = ""
    )
}
