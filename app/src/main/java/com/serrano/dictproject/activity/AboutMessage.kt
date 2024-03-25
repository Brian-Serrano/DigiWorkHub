package com.serrano.dictproject.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serrano.dictproject.customui.ErrorComposable
import com.serrano.dictproject.customui.Loading
import com.serrano.dictproject.customui.OneLineText
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.Message
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.Utils

@Composable
fun AboutMessage(
    navController: NavController,
    paddingValues: PaddingValues,
    message: Message,
    process: ProcessState
) {
    when (process) {
        is ProcessState.Loading -> {
            Loading(paddingValues)
        }
        is ProcessState.Error -> {
            ErrorComposable(navController, paddingValues, process.message)
        }
        is ProcessState.Success -> {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .border(
                            BorderStroke(width = 2.dp, Color.Black),
                            MaterialTheme.shapes.extraSmall
                        )
                ) {
                    Text(
                        text = message.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { navController.navigate("Profile/${message.sender.id}") },
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Icon(
                                    bitmap = Utils.encodedStringToImage(message.sender.image),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                            }
                            OneLineText(
                                text = message.sender.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OneLineText(
                            text = Utils.dateTimeToDateTimeString(message.sentDate)
                        )
                    }
                    Divider(thickness = 2.dp, color = Color.Black)
                    Text(
                        text = message.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        textAlign = TextAlign.Justify
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            OneLineText(text = "Sent to: ")
                            IconButton(
                                onClick = { navController.navigate("Profile/${message.receiver.id}") },
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Icon(
                                    bitmap = Utils.encodedStringToImage(message.receiver.image),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                            }
                            OneLineText(
                                text = message.receiver.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AMPrev() {
    DICTProjectTheme {
        AboutMessage(navController = rememberNavController(), paddingValues = PaddingValues(0.dp), message = Message(), process = ProcessState.Success)
    }
}