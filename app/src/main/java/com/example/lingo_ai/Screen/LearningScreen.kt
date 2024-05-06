package com.example.lingo_ai.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lingo_ai.Viewmodel.LearningViewModel
import com.lazarus.cloudapi.ChatHistoryObj
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun LearningScreen(
    learningViewModel: LearningViewModel,
    contentPadding: PaddingValues
){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val botPad = contentPadding.calculateBottomPadding();
    val learningScreenUiStatevalue = learningViewModel.learningScreenUiState.value
    var selectedIndex = learningScreenUiStatevalue.selectedIndex
    var isChatListInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(Unit){
        if (!isChatListInitialized) {
            scope.launch {
                learningViewModel.getChatList()
                isChatListInitialized = true
            }
        }
    }

    val chatList by learningViewModel.liveChatList.observeAsState(emptyList());
    val currentChat_id by learningViewModel.currentChat_id.observeAsState();
    val chatHistory by learningViewModel.messageHistory.observeAsState(emptyList());

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = botPad)
    ){
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet () {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        Spacer(Modifier.height(12.dp))
                        NavigationDrawerItem(
                            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Plus") },
                            label = { Text(text = "New Chat") },
                            selected = selectedIndex == 0,
                            onClick = {
                                scope.launch { drawerState.close() }
                                learningViewModel.onChangingChat(0, null);
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        )
                        chatList.forEachIndexed { index, it ->
                            NavigationDrawerItem(
                                label = { Text(text = it.chat_id.toString()) },
                                selected = selectedIndex == index + 1,
                                onClick = {
                                    scope.launch {
                                        drawerState.close();
                                        learningViewModel.readChatHistory();
                                    }
                                    learningViewModel.onChangingChat(index + 1, it.chat_id)
                                },
                                badge = {
                                    IconButton(
                                        onClick = {
                                            learningViewModel.onDeleteChat(index, it.chat_id);
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) {
            val message = remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column() {
                    Row(){
                        Icon(
                            imageVector = Icons.Default.Menu, contentDescription = "Paintbrush",
                            modifier = Modifier.
                            clickable {
                                scope.launch { drawerState.open() }
                            }
                        )
                        if (selectedIndex == 0){
                            Text(text = ChangeChatNameOnReceive(currentChat_id = currentChat_id))
                        } else {
                            Text(text = learningScreenUiStatevalue.chat_id.toString())
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                    )
                }
                Column() {
                    if (selectedIndex != 0){
                        println("selected index change to $selectedIndex")
                        ChatBody(
                            chatHistory = chatHistory,
                            modifier = Modifier.weight(10f),
                            learningViewModel = learningViewModel
                        )
                    } else {
                        Spacer(
                            modifier = Modifier.weight(10f)
                        )
                    }
                    MessageInput(
                        messageState = message,
                        onSendMessage = {
                            scope.launch {
                                learningViewModel.onSendMessage(message.value)
                                //learningViewModel.getLatestChat()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        learningViewModel = learningViewModel,
                        chatHistorySize = chatHistory.size
                    )
                }
            }
        }
    }
}

@Composable
fun drawerIndexNotZero(){
    Icon(
        imageVector = Icons.Default.Delete, contentDescription = "Delete",
        modifier = Modifier.clickable {

        }
    )
}

@Composable
internal fun ChatBody(
    chatHistory:List<ChatHistoryObj>,
    modifier: Modifier,
    learningViewModel: LearningViewModel
){

    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = modifier,
        reverseLayout = true
    ) {
        val chatHistorysize = chatHistory.size
        items(chatHistorysize) { i ->
            var isUser = false;
            if (chatHistory[chatHistorysize - 1 - i].role == "user"){
                isUser = true;
            }
            MessageBubble(
                message = chatHistory[chatHistorysize - 1 - i],
                isUser = isUser,
                learningViewModel = learningViewModel,
                chatHistoryObj = chatHistory[chatHistorysize - 1 - i],
                index = chatHistorysize - 1 - i
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatHistoryObj,
    isUser: Boolean,
    learningViewModel: LearningViewModel,
    chatHistoryObj: ChatHistoryObj,
    index: Int
) {
    var roundedCornerShape:RoundedCornerShape;
    val cornerRadius = 16.dp;
    val isPlaying = learningViewModel.learningScreenUiState.value.isPlayingSound;
    val audioPlayingIndex = learningViewModel.learningScreenUiState.value.audioPlayingIndex;
    Row(modifier = Modifier.fillMaxWidth()) {
        if (isUser) {
            Spacer(modifier = Modifier.weight(1f));
            roundedCornerShape = RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius,
                bottomStart = cornerRadius,
                bottomEnd = 0.dp
            )
        } else {
            roundedCornerShape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = cornerRadius,
                bottomStart = cornerRadius,
                bottomEnd = cornerRadius
            )
        }
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentSize(),
            shape = roundedCornerShape,
            color = Color.Blue,
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text = message.content
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    val speedModifier = Modifier.weight(1f)
                    if (!isPlaying && audioPlayingIndex == null && chatHistoryObj.role == "assistant"){
                        Surface(
                            shape = CircleShape,
                            color = Color.Cyan,
                            modifier = speedModifier
                        ) {
                            Text(text = ".75x")
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.Cyan,
                            modifier = speedModifier
                        ) {
                            Text(text = "1x")
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.Cyan,
                            modifier = speedModifier
                        ) {
                            Text(text = "1.25x")
                        }
                    }
                    if (chatHistoryObj.role == "assistant" || learningViewModel.userAudioFileExist(index)){
                        IconButton(
                            onClick = {
                                if (chatHistoryObj.role == "user"){
                                    runBlocking {
                                        learningViewModel.tortureYourselfWithYourOwnVoice(index)
                                    }
                                } else {
                                    println("listening to $index")
                                    runBlocking {
                                        learningViewModel.startListening(chatHistoryObj, index)
                                    }
                                }
                            },
                            modifier = speedModifier
                        ) {
                            Icon(
                                imageVector = if (isPlaying && audioPlayingIndex == index) { Icons.Filled.Pause }
                                            else { Icons.Filled.PlayArrow },
                                contentDescription = "Play/Pause Audio"
                            )
                        }
                        if (isPlaying){
                            //AudioSpectrumView(modifier = Modifier.size(width = 100.dp, height = 50.dp), audioUrl = audioUrl)
                        }
                    } else {
                    }
                }
                Spacer(modifier = Modifier.weight(1f)) // Spacer for horizontal alignment
            }
        }
        if (!isUser){
            Spacer(modifier = Modifier.weight(1f));
        }
    }
}

@Composable
internal fun MessageInput(
    messageState: MutableState<String>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier,
    learningViewModel: LearningViewModel,
    chatHistorySize: Int
) {
    var isMicActive by remember { mutableStateOf(false) }
    val userVoixMessage by learningViewModel.userVoixMessage.observeAsState(null)
    var scope = rememberCoroutineScope();
    val newChat = learningViewModel.learningScreenUiState.value.new_chat
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        IconButton(
            onClick = {
                // Handle microphone click (start/stop speech recognition)
                if(!isMicActive){
                    scope.launch {
                        learningViewModel.onRecordVoice(chatHistorySize - 1);
                    }
                } else {
                    scope.launch {
                        learningViewModel.stopAudioRecord();
                    }
                }
                isMicActive = !isMicActive
            },
            modifier = Modifier.weight(0.1f),
        ) {
            Icon(
                imageVector = if (isMicActive) Icons.Filled.Mic else Icons.Filled.MicOff,
                contentDescription = "Microphone"
            )
        }
        TextField(
            value = messageState.value,
            onValueChange = { messageState.value = it },
            modifier = Modifier
                .weight(0.8f)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp)),
            placeholder = { Text("Type your message") },
        )
        if (messageState.value.isNotEmpty()) {
            IconButton(
                onClick = { onSendMessage(messageState.value) },
                modifier = Modifier.weight(0.1f),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send, // Use Send icon from Jetpack Compose icons
                    contentDescription = "Send message"
                )
            }
        }
    }
}

@Composable
internal fun ChangeChatNameOnReceive(currentChat_id:Long?):String{
    if (currentChat_id == null){
        return("New Chat")
    } else {
        return currentChat_id.toString();
    }
}
