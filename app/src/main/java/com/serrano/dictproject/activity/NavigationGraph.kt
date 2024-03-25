package com.serrano.dictproject.activity

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CallReceived
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.serrano.dictproject.R
import com.serrano.dictproject.customui.BottomBar
import com.serrano.dictproject.customui.CustomScaffold
import com.serrano.dictproject.customui.Drawer
import com.serrano.dictproject.customui.WindowInfo
import com.serrano.dictproject.viewmodel.AboutMessageViewModel
import com.serrano.dictproject.viewmodel.AboutTaskViewModel
import com.serrano.dictproject.viewmodel.AddTaskViewModel
import com.serrano.dictproject.viewmodel.DashboardViewModel
import com.serrano.dictproject.viewmodel.InboxViewModel
import com.serrano.dictproject.viewmodel.ProfileViewModel
import com.serrano.dictproject.viewmodel.SendMessageViewModel
import com.serrano.dictproject.viewmodel.SharedViewModel
import com.serrano.dictproject.viewmodel.SignupViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

@Composable
fun NavigationGraph(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    sharedViewModel: SharedViewModel,
    windowInfo: WindowInfo
) {
    NavHost(navController = navController, startDestination = "Splash") {
        composable(route = "Splash") {
            val preferences by sharedViewModel.preferences.collectAsState()

            var startAnimation by remember { mutableStateOf(false) }
            val alphaAnimation = animateFloatAsState(
                targetValue = if (startAnimation) 1f else 0f,
                animationSpec = tween(3000),
                label = ""
            )
            LaunchedEffect(key1 = true) {

                startAnimation = true

                while (preferences == null) {
                    delay(500)
                }

                navController.popBackStack()

                when {
                    preferences!!.authToken.isNotEmpty() -> navController.navigate("Dashboard")
                    else -> navController.navigate("Signup")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alphaAnimation.value)
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.department_of_information_and_communications_technology__dict_
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .size(300.dp)
                )
            }
        }
        composable(route = "Signup") {
            val signupViewModel = hiltViewModel<SignupViewModel>()

            val signupState by signupViewModel.signupState.collectAsState()

            SelectionContainer {
                Signup(
                    navController = navController,
                    signupState = signupState,
                    updateSignupState = signupViewModel::updateSignupState,
                    signup = signupViewModel::signup,
                    login = signupViewModel::login
                )
            }
        }
        composable(route = "Dashboard") {
            val dashboardViewModel = hiltViewModel<DashboardViewModel>()

            LaunchedEffect(Unit) {
                dashboardViewModel.getTasks()
                dashboardViewModel.getCreatedTasks()
            }

            val preferences by sharedViewModel.preferences.collectAsState()
            val sharedState by sharedViewModel.sharedState.collectAsState()
            val process by dashboardViewModel.processState.collectAsState()
            val process2 by dashboardViewModel.processState2.collectAsState()
            val dashboardState by dashboardViewModel.dashboardState.collectAsState()
            val tasks by dashboardViewModel.modifiedTasks.collectAsState()
            val createdTasks by dashboardViewModel.modifiedCreatedTasks.collectAsState()
            val dialogsState by dashboardViewModel.dialogsState.collectAsState()
            val dashboardDialogs by dashboardViewModel.dialogState.collectAsState()
            val rawTasks by dashboardViewModel.tasks.collectAsState()
            val rawCreatedTasks by dashboardViewModel.createdTasks.collectAsState()

            Drawer(
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                navController = navController,
                user = preferences,
                context = context,
                selected = "Dashboard"
            ) {
                CustomScaffold(
                    user = preferences,
                    coroutineScope = coroutineScope,
                    drawerState = drawerState,
                    navController = navController,
                    content = { paddingValues ->
                        Dashboard(
                            windowInfo = windowInfo,
                            rawTasks = rawTasks,
                            rawCreatedTasks = rawCreatedTasks,
                            dashboardDialogs = dashboardDialogs,
                            navController = navController,
                            paddingValues = paddingValues,
                            process = process,
                            process2 = process2,
                            dashboardState = dashboardState,
                            tasks = tasks,
                            createdTasks = createdTasks,
                            dialogsState = dialogsState,
                            sharedState = sharedState,
                            updateDialogState = dashboardViewModel::updateDialogState,
                            updateSharedState = sharedViewModel::updateSharedState,
                            updateRadioDialogState = dashboardViewModel::updateRadioDialogState,
                            updateGroupDropdown = dashboardViewModel::updateGroupDropdown,
                            filterTab = dashboardViewModel::filterTab,
                            filterAllTabs = dashboardViewModel::filterAllTabs,
                            updateFilterDropdown = dashboardViewModel::updateFilterDropdown,
                            updateOptionsDropdown = dashboardViewModel::updateOptionsDropdown,
                            updateSortDropdown = dashboardViewModel::updateSortDropdown,
                            updateCollapsible = dashboardViewModel::updateCollapsible,
                            updateEditNameDialogState = dashboardViewModel::updateEditNameDialogState,
                            updateSearchDialogState = dashboardViewModel::updateSearchDialogState,
                            updateDateDialogState = dashboardViewModel::updateDateDialogState,
                            updateIsFilterDropdown = dashboardViewModel::updateIsFilterDropdown,
                            updateOptionsFilterDropdown = dashboardViewModel::updateOptionsFilterDropdown,
                            changeName = dashboardViewModel::changeName,
                            changeAssignee = dashboardViewModel::changeAssignee,
                            updateSearchState = dashboardViewModel::updateSearchState,
                            updateViewAssigneeDialogState = dashboardViewModel::updateViewAssigneeDialogState,
                            searchUser = dashboardViewModel::searchUser,
                            changeDue = dashboardViewModel::changeDue,
                            changePriority = dashboardViewModel::changePriority,
                            changeStatus = dashboardViewModel::changeStatus,
                            changeType = dashboardViewModel::changeType,
                            updateTasks = dashboardViewModel::updateTasks,
                            updateCreatedTasks = dashboardViewModel::updateCreatedTasks
                        )
                    },
                    floatingButton = {
                        FloatingActionButton(onClick = { navController.navigate("AddTask") }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        }
                    },
                    bottomBar = BottomBar(
                        items = listOf("ASSIGNED", "CREATED"),
                        icons = listOf(
                            listOf(Icons.Outlined.Assignment, Icons.Filled.Assignment),
                            listOf(Icons.Outlined.AddCircle, Icons.Filled.AddCircle)
                        ),
                        bottomBarIdx = sharedState.dashboardBottomBarIdx,
                        onClick = { sharedViewModel.updateSharedState(sharedState.copy(dashboardBottomBarIdx = it)) }
                    )
                )
            }
        }
        composable(route = "Inbox") {
            val inboxViewModel = hiltViewModel<InboxViewModel>()

            LaunchedEffect(Unit) {
                inboxViewModel.getSentMessages()
                inboxViewModel.getReceivedMessages()
            }

            val preferences by sharedViewModel.preferences.collectAsState()
            val sharedState by sharedViewModel.sharedState.collectAsState()
            val process by inboxViewModel.processState.collectAsState()
            val process2 by inboxViewModel.processState2.collectAsState()
            val sentMessages by inboxViewModel.sentMessages.collectAsState()
            val receivedMessages by inboxViewModel.receivedMessage.collectAsState()

            Drawer(
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                navController = navController,
                user = preferences,
                context = context,
                selected = "Inbox"
            ) {
                CustomScaffold(
                    user = preferences,
                    coroutineScope = coroutineScope,
                    drawerState = drawerState,
                    navController = navController,
                    content = {
                        Inbox(
                            navController = navController,
                            paddingValues = it,
                            process = process,
                            process2 = process2,
                            sentMessages = sentMessages,
                            receivedMessages = receivedMessages,
                            sharedState = sharedState
                        )
                    },
                    floatingButton = {
                        FloatingActionButton(onClick = { navController.navigate("SendMessage") }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        }
                    },
                    bottomBar = BottomBar(
                        items = listOf("RECEIVED", "SENT"),
                        icons = listOf(
                            listOf(Icons.Outlined.CallReceived, Icons.Filled.CallReceived),
                            listOf(Icons.Outlined.Send, Icons.Filled.Send)
                        ),
                        bottomBarIdx = sharedState.messageBottomBarIdx,
                        onClick = { sharedViewModel.updateSharedState(sharedState.copy(messageBottomBarIdx = it)) }
                    )
                )
            }
        }
        composable(
            route = "AboutTask/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { entry ->
            val aboutTaskViewModel = hiltViewModel<AboutTaskViewModel>()

            LaunchedEffect(Unit) {
                aboutTaskViewModel.getTaskInfo(entry.arguments!!.getInt("taskId"))
            }

            val preferences by sharedViewModel.preferences.collectAsState()
            val process by aboutTaskViewModel.processState.collectAsState()
            val task by aboutTaskViewModel.task.collectAsState()
            val dialogsState by aboutTaskViewModel.dialogsState.collectAsState()
            val aboutTaskState by aboutTaskViewModel.aboutTaskState.collectAsState()
            val aboutTaskDialogs by aboutTaskViewModel.dialogState.collectAsState()

            Drawer(
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                navController = navController,
                user = preferences,
                context = context,
                selected = "Dashboard"
            ) {
                CustomScaffold(
                    user = preferences,
                    coroutineScope = coroutineScope,
                    drawerState = drawerState,
                    navController = navController,
                    content = {
                        AboutTask(
                            windowInfo = windowInfo,
                            aboutTaskDialogs = aboutTaskDialogs,
                            preferences = preferences!!,
                            navController = navController,
                            paddingValues = it,
                            context = context,
                            process = process,
                            task = task,
                            dialogsState = dialogsState,
                            aboutTaskState = aboutTaskState,
                            updateTabIdx = aboutTaskViewModel::updateTabIdx,
                            updateAddSubtaskState = aboutTaskViewModel::updateAddSubtaskState,
                            updateAddCommentState = aboutTaskViewModel::updateAddCommentState,
                            updateAddChecklistState = aboutTaskViewModel::updateAddChecklistState,
                            updateAddAttachmentState = aboutTaskViewModel::updateAddAttachmentState,
                            updateDialogState = aboutTaskViewModel::updateDialogState,
                            updateTask = aboutTaskViewModel::updateTask,
                            updateRadioDialogState = aboutTaskViewModel::updateRadioDialogState,
                            updateEditNameDialogState = aboutTaskViewModel::updateEditNameDialogState,
                            updateDateDialogState = aboutTaskViewModel::updateDateDialogState,
                            updateSearchDialogState = aboutTaskViewModel::updateSearchDialogState,
                            updateSearchState = aboutTaskViewModel::updateSearchState,
                            updateViewAssigneeDialogState = aboutTaskViewModel::updateViewAssigneeDialogState,
                            changeAssignee = aboutTaskViewModel::changeAssignee,
                            changeName = aboutTaskViewModel::changeName,
                            searchUser = aboutTaskViewModel::searchUser,
                            changeDue = aboutTaskViewModel::changeDue,
                            changePriority = aboutTaskViewModel::changePriority,
                            changeStatus = aboutTaskViewModel::changeStatus,
                            changeType = aboutTaskViewModel::changeType,
                            changeDescription = aboutTaskViewModel::changeDescription,
                            sendComment = aboutTaskViewModel::sendComment,
                            addChecklist = aboutTaskViewModel::addChecklist,
                            addSubtask = aboutTaskViewModel::addSubtask,
                            uploadAttachment = aboutTaskViewModel::uploadAttachment,
                            changeSubtaskDescription = aboutTaskViewModel::changeSubtaskDescription,
                            changeSubtaskPriority = aboutTaskViewModel::changeSubtaskPriority,
                            changeSubtaskDueDate = aboutTaskViewModel::changeSubtaskDueDate,
                            editSubtaskAssignees = aboutTaskViewModel::editSubtaskAssignees,
                            changeSubtaskType = aboutTaskViewModel::changeSubtaskType,
                            changeSubtaskStatus = aboutTaskViewModel::changeSubtaskStatus,
                            toggleChecklist = aboutTaskViewModel::toggleChecklist,
                            likeComment = aboutTaskViewModel::likeComment,
                            downloadAttachment = aboutTaskViewModel::downloadAttachment
                        )
                    }
                )
            }
        }
        composable(route = "AddTask") {
            val addTaskViewModel = hiltViewModel<AddTaskViewModel>()

            val preferences by sharedViewModel.preferences.collectAsState()
            val dialogsState by addTaskViewModel.dialogsState.collectAsState()
            val addTaskState by addTaskViewModel.addTaskState.collectAsState()
            val addTaskDialogs by addTaskViewModel.dialogState.collectAsState()

            Drawer(
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                navController = navController,
                user = preferences,
                context = context,
                selected = "Dashboard"
            ) {
                CustomScaffold(
                    user = preferences,
                    coroutineScope = coroutineScope,
                    drawerState = drawerState,
                    navController = navController,
                    content = {
                        AddTask(
                            windowInfo = windowInfo,
                            addTaskDialogs = addTaskDialogs,
                            navController = navController,
                            paddingValues = it,
                            dialogsState = dialogsState,
                            addTaskState = addTaskState,
                            updateDialogState = addTaskViewModel::updateDialogState,
                            updateRadioDialogState = addTaskViewModel::updateRadioDialogState,
                            updateEditNameDialogState = addTaskViewModel::updateEditNameDialogState,
                            updateDateDialogState = addTaskViewModel::updateDateDialogState,
                            updateSearchDialogState = addTaskViewModel::updateSearchDialogState,
                            updateViewAssigneeDialogState = addTaskViewModel::updateViewAssigneeDialogState,
                            updateTaskState = addTaskViewModel::updateTaskState,
                            updateSearchState = addTaskViewModel::updateSearchState,
                            searchUser = addTaskViewModel::searchUser,
                            addTask = addTaskViewModel::addTask
                        )
                    }
                )
            }
        }
        composable(
            route = "Profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { entry ->
            val profileViewModel = hiltViewModel<ProfileViewModel>()

            LaunchedEffect(Unit) {
                profileViewModel.getUser(entry.arguments!!.getInt("userId"))
            }

            val preferences by sharedViewModel.preferences.collectAsState()
            val process by profileViewModel.processState.collectAsState()
            val user by profileViewModel.user.collectAsState()
            val profileDialogs by profileViewModel.dialogState.collectAsState()
            val profileState by profileViewModel.profileState.collectAsState()

            Drawer(
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                navController = navController,
                user = preferences,
                context = context,
                selected = "Dashboard"
            ) {
                CustomScaffold(
                    user = preferences,
                    coroutineScope = coroutineScope,
                    drawerState = drawerState,
                    navController = navController,
                    content = {
                        Profile(
                            preferences = preferences!!,
                            navController = navController,
                            context = context,
                            paddingValues = it,
                            process = process,
                            user = user,
                            profileDialogs = profileDialogs,
                            profileState = profileState,
                            updateUser = profileViewModel::updateUser,
                            updateDialogState = profileViewModel::updateDialogState,
                            updateProfileState = profileViewModel::updateProfileState,
                            changeUserName = profileViewModel::changeUserName,
                            changeUserRole = profileViewModel::changeUserRole,
                            uploadImage = profileViewModel::uploadImage,
                            changePreferencesName = profileViewModel::changePreferencesName,
                            changePreferencesImage = profileViewModel::changePreferencesImage
                        )
                    }
                )
            }
        }
        composable(
            route = "AboutMessage/{messageId}",
            arguments = listOf(navArgument("messageId") { type = NavType.IntType })
        ) { entry ->
            val aboutMessageViewModel = hiltViewModel<AboutMessageViewModel>()

            LaunchedEffect(Unit) {
                aboutMessageViewModel.getMessage(entry.arguments!!.getInt("messageId"))
            }

            val preferences by sharedViewModel.preferences.collectAsState()
            val process by aboutMessageViewModel.processState.collectAsState()
            val message by aboutMessageViewModel.message.collectAsState()

            Drawer(
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                navController = navController,
                user = preferences,
                context = context,
                selected = "Inbox"
            ) {
                CustomScaffold(
                    user = preferences,
                    coroutineScope = coroutineScope,
                    drawerState = drawerState,
                    navController = navController,
                    content = {
                        AboutMessage(
                            navController = navController,
                            paddingValues = it,
                            message = message,
                            process = process
                        )
                    }
                )
            }
        }
        composable(route = "SendMessage") {
            val sendMessageViewModel = hiltViewModel<SendMessageViewModel>()

            val preferences by sharedViewModel.preferences.collectAsState()
            val sendMessageState by sendMessageViewModel.sendMessageState.collectAsState()
            val sendMessageDialogs by sendMessageViewModel.dialogState.collectAsState()

            Drawer(
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                navController = navController,
                user = preferences,
                context = context,
                selected = "Inbox"
            ) {
                CustomScaffold(
                    user = preferences,
                    coroutineScope = coroutineScope,
                    drawerState = drawerState,
                    navController = navController,
                    content = {
                        SendMessage(
                            navController = navController,
                            paddingValues = it,
                            sendMessageState = sendMessageState,
                            sendMessageDialogs = sendMessageDialogs,
                            updateSendMessageState = sendMessageViewModel::updateSendMessageState,
                            updateDialogState = sendMessageViewModel::updateDialogState,
                            sendMessage = sendMessageViewModel::sendMessage,
                            searchUser = sendMessageViewModel::searchUser
                        )
                    }
                )
            }
        }
    }
}