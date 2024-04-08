package com.serrano.dictproject.customui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.RadioButtonDialogState

@Composable
fun RadioButtonDialog(
    text: String,
    radioButtonDialogState: RadioButtonDialogState,
    onApplyClick: (Int, String) -> Unit,
    onDismissRequest: () -> Unit,
    onRadioSelect: (String) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0x55000000)))
    Dialog(onDismissRequest = onDismissRequest) {
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
                    text = "Edit $text",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                radioButtonDialogState.options.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = radioButtonDialogState.selected == it,
                            onClick = { onRadioSelect(it) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        OneLineText(it)
                    }
                }

                Row {
                    CustomButton(
                        text = "APPLY",
                        onClick = {
                            onDismissRequest()
                            onApplyClick(radioButtonDialogState.taskId, radioButtonDialogState.selected)
                        }
                    )
                    CustomButton(
                        text = "CANCEL",
                        onClick = onDismissRequest
                    )
                }
            }
        }
    }
}