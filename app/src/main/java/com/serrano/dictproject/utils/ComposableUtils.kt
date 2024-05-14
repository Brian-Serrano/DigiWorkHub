package com.serrano.dictproject.utils

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable

/**
 * Custom sticky header for LazyVerticalGrid items
 *
 * @param[content] user interface content of the header
 */
fun LazyGridScope.header(content: @Composable LazyGridItemScope.() -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }, content = content)
}