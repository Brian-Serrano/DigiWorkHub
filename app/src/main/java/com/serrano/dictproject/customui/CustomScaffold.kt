package com.serrano.dictproject.customui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.serrano.dictproject.datastore.Preferences
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Scaffold with top bar used by all pages except signup and splash
 *
 * @param[user] The user information to show in top bar
 * @param[coroutineScope] Coroutine used for opening the drawer when clicking menu icon
 * @param[drawerState] What drawer to open
 * @param[navController] Used for navigating in own profile when clicking own image
 * @param[content] The content of scaffold (components shown below the top bar)
 * @param[floatingButton] Optional floating button. Used in dashboard and inbox when user wants to create task or send message.
 * @param[bottomBar] Optional bottom bar. Used in dashboard and inbox page for assigned and created tasks and sent and received messages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomScaffold(
    user: Preferences?,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit,
    floatingButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(color = Color.Yellow)
                            ) {
                                append("DigiWork")
                            }
                            withStyle(
                                style = SpanStyle(color = Color.Blue)
                            ) {
                                append("Hub")
                            }
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                navigationIcon = {
                    when (user) {
                        null, Preferences() -> {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        else -> {
                            IconButton(onClick = { navController.navigate("${Routes.PROFILE}/${user.id}") }) {
                                Image(
                                    bitmap = FileUtils.encodedStringToImage(user.image),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = floatingButton,
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = bottomBar,
        content = content
    )
}