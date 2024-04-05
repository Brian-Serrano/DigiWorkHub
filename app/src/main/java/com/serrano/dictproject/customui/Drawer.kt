package com.serrano.dictproject.customui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.serrano.dictproject.datastore.Preferences
import com.serrano.dictproject.utils.DrawerData
import com.serrano.dictproject.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    navController: NavController,
    user: Preferences?,
    onLogout: () -> Unit,
    selected: String,
    content: @Composable () -> Unit
) {
    val singleTop: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
    }

    val items = listOf(
        DrawerData(Icons.Filled.Dashboard, "Dashboard") {
            navController.navigate(Routes.DASHBOARD, singleTop)
        },
        DrawerData(Icons.Filled.Inbox, "Inbox") {
            navController.navigate(Routes.INBOX, singleTop)
        },
        DrawerData(Icons.Filled.Settings, "Settings") {
            navController.navigate(Routes.SETTINGS, singleTop)
        },
        DrawerData(Icons.AutoMirrored.Filled.Logout, "Log Out") {
            navController.navigate(Routes.SIGNUP) {
                popUpTo(navController.graph.id) {
                    inclusive = false
                }
                launchSingleTop = true
            }
            onLogout()
        }
    )

    SelectionContainer {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    drawerContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (user) {
                            null, Preferences() -> {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            else -> {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            selected = item.name == selected,
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.close()
                                    item.action()
                                }
                            },
                            modifier = Modifier.padding(
                                paddingValues = NavigationDrawerItemDefaults.ItemPadding
                            ),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                                unselectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onTertiary,
                                unselectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onTertiary,
                                unselectedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                selectedBadgeColor = MaterialTheme.colorScheme.onTertiary,
                                unselectedBadgeColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }
            },
            content = content
        )
    }
}