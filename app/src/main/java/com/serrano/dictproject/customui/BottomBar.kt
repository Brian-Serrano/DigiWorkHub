package com.serrano.dictproject.customui

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomBar(
    items: List<String>,
    icons: List<List<ImageVector>>,
    bottomBarIdx: Int,
    onClick: (Int) -> Unit
): @Composable () -> Unit {
    return {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = bottomBarIdx == index,
                    onClick = { onClick(index) },
                    label = {
                        Text(text = item)
                    },
                    alwaysShowLabel = true,
                    icon = {
                        Icon(imageVector = icons[index][if (bottomBarIdx == index) 1 else 0], contentDescription = null)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.tertiary,
                        indicatorColor = MaterialTheme.colorScheme.tertiary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                        unselectedTextColor = MaterialTheme.colorScheme.tertiary,
                        disabledIconColor = MaterialTheme.colorScheme.tertiary,
                        disabledTextColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }
    }
}