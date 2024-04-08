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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.ui.theme.DICTProjectTheme

@Composable
fun ConfirmDialog(
    id: Int,
    placeholder: String,
    onYesClick: (Int) -> Unit,
    onCancelClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0x55000000)))
    Dialog(onDismissRequest = onCancelClick) {
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
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomButton(
                        text = "YES",
                        onClick = {
                            onCancelClick()
                            onYesClick(id)
                        }
                    )
                    CustomButton(
                        text = "CANCEL",
                        onClick = onCancelClick
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CDPrev() {
    DICTProjectTheme {
        ConfirmDialog(id = 0, placeholder = "task", onYesClick = {}) {

        }
    }
}