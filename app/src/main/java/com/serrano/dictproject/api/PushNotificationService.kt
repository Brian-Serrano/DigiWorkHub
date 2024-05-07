package com.serrano.dictproject.api

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.serrano.dictproject.R
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.NotificationTokenBody
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {

    @Inject lateinit var apiRepository: ApiRepository
    @Inject lateinit var preferencesRepository: PreferencesRepository
    @Inject lateinit var context: Context
    private val job = SupervisorJob()

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(job).launch {
            val data = preferencesRepository.getData().first()
            if (data.email.isNotEmpty() && data.password.isNotEmpty() && data.authToken.isNotEmpty()) {
                if (MiscUtils.checkToken(data.authToken)) {
                    MiscUtils.checkAuthentication(context, preferencesRepository, apiRepository)
                } else {
                    apiRepository.updateNotificationsToken(NotificationTokenBody(token))
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val channelId = "digiwork-hub"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setVibrate(LongArray(5) { 1000 })
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(channelId, "digiwork-hub-push-notification", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}