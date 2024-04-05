package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.WindowInfo
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.button.TextWithEditButton
import com.serrano.dictproject.customui.dropdown.DueDropDown
import com.serrano.dictproject.customui.dropdown.PriorityDropDown
import com.serrano.dictproject.customui.dropdown.StatusDropDown
import com.serrano.dictproject.customui.dropdown.TypeDropDown
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.TaskPartDTO

@Composable
fun DashboardList(
    windowInfo: WindowInfo,
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
    val assigneesComposable: @Composable RowScope.() -> Unit = {
        Row {
            Row(horizontalArrangement = Arrangement.spacedBy((-25).dp)) {
                task.assignees.forEach {
                    IconButton(onClick = openViewDialog) {
                        Icon(
                            bitmap = FileUtils.encodedStringToImage(it.image),
                            contentDescription = null,
                            tint = Color.Unspecified
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
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navigateToProfile(task.creator.id) },
                modifier = Modifier
                    .size(100.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    bitmap = FileUtils.encodedStringToImage(task.creator.image),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(100.dp)
                )
            }
            Column {
                TextWithEditButton(
                    text = task.title,
                    onEditButtonClick = onTitleClick,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    isOneLine = false
                )
                when (windowInfo.screenWidthInfo) {
                    is WindowInfo.WindowType.Compact -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            TypeDropDown(text = task.type, onClick = onTypeClick)
                            StatusDropDown(text = task.status, onClick = onStatusClick)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            PriorityDropDown(text = task.priority, onClick = onPriorityClick)
                            DueDropDown(date = task.due, onClick = onDueClick)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            assigneesComposable()
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            CustomButton(text = "VIEW", onClick = onViewClick)
                        }
                    }
                    is WindowInfo.WindowType.Medium -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            DueDropDown(date = task.due, onClick = onDueClick)
                            StatusDropDown(text = task.status, onClick = onStatusClick)
                            PriorityDropDown(text = task.priority, onClick = onPriorityClick)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            assigneesComposable()
                            TypeDropDown(text = task.type, onClick = onTypeClick)
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            CustomButton(text = "VIEW", onClick = onViewClick)
                        }
                    }
                    is WindowInfo.WindowType.Expanded -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            assigneesComposable()
                            StatusDropDown(text = task.status, onClick = onStatusClick)
                            PriorityDropDown(text = task.priority, onClick = onPriorityClick)
                            DueDropDown(date = task.due, onClick = onDueClick)
                            TypeDropDown(text = task.type, onClick = onTypeClick)
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            CustomButton(text = "VIEW", onClick = onViewClick)
                        }
                    }
                }
            }
        }
    }
}

//@PreviewScreenSizes
//@Composable
//fun DLPrev() {
//    DICTProjectTheme {
//        DashboardList(
//            windowInfo = RememberWindowInfo(),
//            task = TaskPartDTO(),
//            navigateToProfile = { /*TODO*/ },
//            openViewDialog = { /*TODO*/ },
//            onTitleClick = { /*TODO*/ },
//            onStatusClick = { /*TODO*/ },
//            onAssigneeClick = { /*TODO*/ },
//            onPriorityClick = { /*TODO*/ },
//            onDueClick = { /*TODO*/ },
//            onTypeClick = { /*TODO*/ }) {
//
//        }
//    }
//}