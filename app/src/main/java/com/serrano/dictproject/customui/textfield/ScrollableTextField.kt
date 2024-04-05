package com.serrano.dictproject.customui.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.ui.theme.DICTProjectTheme

@Composable
fun ScrollableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = {
            Text(text = placeholderText, style = MaterialTheme.typography.bodyMedium)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .heightIn(0.dp, 200.dp)
            .verticalScroll(rememberScrollState()),
        colors = InputFieldColors()
    )
}

@Preview
@Composable
fun ScrollTFPrev() {
    DICTProjectTheme {
        ScrollableTextField(value = "Launch a comprehensive circular economy implementation program aimed at transforming our business operations, supply chain, and waste management practices to minimize waste generation, maximize resource efficiency, and create sustainable value chains. This ambitious initiative involves reimagining product design, manufacturing processes, and consumption patterns to eliminate waste, extend product lifecycles, and regenerate natural systems. Collaborate with product designers, engineers, and supply chain partners to redesign products and packaging for durability, reparability, and recyclability, incorporating principles of eco-design, biomimicry, and cradle-to-cradle design into product development processes.", onValueChange = {}, placeholderText = "TEST")
    }
}