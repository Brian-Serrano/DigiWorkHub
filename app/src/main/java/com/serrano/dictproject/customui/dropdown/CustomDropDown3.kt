package com.serrano.dictproject.customui.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.ui.theme.DICTProjectTheme

@Composable
fun RowScope.CustomDropDown3(
    selected: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .height(IntrinsicSize.Min)
            .clip(MaterialTheme.shapes.extraSmall)
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                MaterialTheme.shapes.extraSmall
            )
            .fillMaxWidth()
            .weight(1f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OneLineText(
                text = selected,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(10.dp)
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Preview
@Composable
fun CC3Prev() {
    DICTProjectTheme {
        Row {
            CustomDropDown3(
                selected = "IN PROGRESS",
                onClick = { /*TODO*/ }
            )
        }
    }
}