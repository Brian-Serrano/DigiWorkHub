package com.serrano.dictproject.customui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.dropdown.CustomDropDown
import com.serrano.dictproject.customui.dropdown.CustomDropDown4
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.DashboardState
import com.serrano.dictproject.utils.DropDownMultiselect
import com.serrano.dictproject.utils.DropDownState

@Composable
fun FilterDialog(
    dashboardState: DashboardState,
    removeDialog: () -> Unit,
    updateIsFilterDropdown: (DropDownState) -> Unit,
    updateOptionsFilterDropdown : (DropDownMultiselect) -> Unit,
    onApplyClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0x55000000)))
    Dialog(onDismissRequest = removeDialog) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .height(IntrinsicSize.Min)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OneLineText(
                    text = "Filters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    CustomDropDown(
                        prefix = "INCLUDE",
                        dropDownState = dashboardState.isFilterDropDown,
                        icon = Icons.Filled.Add,
                        onArrowClick = {
                            updateIsFilterDropdown(dashboardState.isFilterDropDown.copy(expanded = true))
                        },
                        onDismissRequest = {
                            updateIsFilterDropdown(dashboardState.isFilterDropDown.copy(expanded = false))
                        },
                        onItemSelect = {
                            updateIsFilterDropdown(dashboardState.isFilterDropDown.copy(selected = it, expanded = false))
                        }
                    )
                }
                Row {
                    CustomDropDown4(
                        dropDown = dashboardState.optionsFilterDropDown,
                        icon = Icons.Filled.FormatListNumbered,
                        onArrowClick = {
                            updateOptionsFilterDropdown(dashboardState.optionsFilterDropDown.copy(expanded = true))
                        },
                        onDismissRequest = {
                            updateOptionsFilterDropdown(dashboardState.optionsFilterDropDown.copy(expanded = false))
                        },
                        onItemSelect = { item ->
                            updateOptionsFilterDropdown(
                                dashboardState.optionsFilterDropDown.copy(
                                    selected = if (dashboardState.optionsFilterDropDown.selected.any { it == item }) {
                                        dashboardState.optionsFilterDropDown.selected - item
                                    } else {
                                        dashboardState.optionsFilterDropDown.selected + item
                                    },
                                    expanded = false
                                )
                            )
                        }
                    )
                }
                Row {
                    CustomButton(
                        text = "APPLY",
                        onClick = {
                            removeDialog()
                            onApplyClick()
                        }
                    )
                    CustomButton(
                        text = "CANCEL",
                        onClick = removeDialog
                    )
                }
            }
        }
    }
}