package com.serrano.dictproject.customui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

/**
 * Used in about task for comments, checklists, subtasks, attachments tabs
 *
 * @param[tabIndex] Selected tab
 * @param[tabs] The tab names
 * @param[onTabClick] Select the tab if user clicks it
 */
@Composable
fun CustomTab(
    tabIndex: Int,
    tabs: List<String>,
    onTabClick: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = tabIndex,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                selected = tabIndex == index,
                onClick = { onTabClick(index) },
                selectedContentColor = if (tabIndex == index) Color.White else Color.Black
            )
        }
    }
}