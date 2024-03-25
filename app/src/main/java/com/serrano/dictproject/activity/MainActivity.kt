package com.serrano.dictproject.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.dataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.serrano.dictproject.customui.RememberWindowInfo
import com.serrano.dictproject.datastore.PreferencesSerializer
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

val Context.preferencesDataStore by dataStore("preferences.json", PreferencesSerializer())

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DICTProjectTheme {
                NavigationGraph(
                    navController = rememberNavController(),
                    coroutineScope = rememberCoroutineScope(),
                    drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
                    context = this,
                    sharedViewModel = hiltViewModel<SharedViewModel>(),
                    windowInfo = RememberWindowInfo()
                )
            }
        }
    }
}