package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.button.TextWithEditButton
import com.serrano.dictproject.customui.dropdown.DueDropDown
import com.serrano.dictproject.customui.dropdown.PriorityDropDown
import com.serrano.dictproject.customui.dropdown.StatusDropDown
import com.serrano.dictproject.customui.dropdown.TypeDropDown
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.TaskPartDTO

@Composable
fun DashboardGrid(
    task: TaskPartDTO,
    navigateToProfile: (Int) -> Unit,
    openViewDialog: () -> Unit,
    onTitleClick: () -> Unit,
    onStatusClick: () -> Unit,
    onAssigneeClick: () -> Unit,
    onPriorityClick: () -> Unit,
    onDueClick: () -> Unit,
    onTypeClick: () -> Unit,
    onViewClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { navigateToProfile(task.creator.id) },
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    bitmap = FileUtils.encodedStringToImage(task.creator.image),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(50.dp)
                )
            }
            Row {
                Row(horizontalArrangement = Arrangement.spacedBy((-15).dp)) {
                    task.assignees.forEach {
                        IconButton(
                            onClick = openViewDialog,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                bitmap = FileUtils.encodedStringToImage(it.image),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
                IconButton(
                    onClick = onAssigneeClick,
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            PriorityDropDown(text = task.priority, onClick = onPriorityClick)
            DueDropDown(date = task.due, onClick = onDueClick)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            TypeDropDown(text = task.type, onClick = onTypeClick)
            StatusDropDown(text = task.status, onClick = onStatusClick)
        }
        Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        TextWithEditButton(
            text = task.title,
            onEditButtonClick = onTitleClick,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            isOneLine = false
        )
        CustomButton(
            text = "VIEW",
            onClick = onViewClick,
            modifier = Modifier.align(Alignment.End)
        )
    }
}