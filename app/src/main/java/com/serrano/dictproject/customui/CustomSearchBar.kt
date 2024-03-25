package com.serrano.dictproject.customui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.utils.SearchState
import com.serrano.dictproject.utils.SearchUserDialogState
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    placeHolder: String,
    searchState: SearchState,
    searchUserDialogState: SearchUserDialogState,
    onUserClick: (Int) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onTrailingIconClick: () -> Unit,
    onUserAdd: (User) -> Unit
) {
    SearchBar(
        query = searchState.searchQuery,
        onQueryChange = onQueryChange,
        active = searchState.isActive,
        onSearch = onSearch,
        onActiveChange = onActiveChange,
        placeholder = {
            OneLineText(text = placeHolder, style = MaterialTheme.typography.bodySmall)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            Row {
                if (searchState.isActive) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .clickable(onClick = onTrailingIconClick)
                    )
                }
            }
        },
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            dividerColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        LazyColumn {
            items(items = searchState.results) { user ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onUserClick(user.id) }) {
                        Icon(
                            bitmap = Utils.encodedStringToImage(user.image),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                    OneLineText(
                        text = user.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    if (searchUserDialogState.users.none { user == it }) {
                        IconButton(onClick = { onUserAdd(user) }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = Color.Green
                            )
                        }
                    }
                }
            }
        }
    }
}