package com.serrano.dictproject.customui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.serrano.dictproject.customui.button.CustomButton

/**
 * Component shown when the process state is error or there are something wrong with requesting data from server or in room database
 *
 * @param[navController] Used when the user wants to navigate back
 * @param[paddingValues] This is used to move contents down (where it should and can see it), there is top bar on page
 * @param[message] The error message
 * @param[refreshState] State for the swipe refresh component
 * @param[onRefresh] Refresh the page (request again from server)
 */
@Composable
fun ErrorComposable(
    navController: NavController,
    paddingValues: PaddingValues,
    message: String,
    refreshState: SwipeRefreshState,
    onRefresh: () -> Unit
) {
    val activity = LocalContext.current as Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        SwipeRefresh(state = refreshState, onRefresh = onRefresh) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
                CustomButton(
                    text = "Go back",
                    onClick = {
                        if (!navController.popBackStack()) {
                            activity.finish()
                        }
                    }
                )
            }
        }
    }
}