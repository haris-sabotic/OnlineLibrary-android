package com.ets.onlinebiblioteka.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ets.onlinebiblioteka.LoginActivity
import com.ets.onlinebiblioteka.MainActivity
import com.ets.onlinebiblioteka.R
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.models.filters.Autor
import com.ets.onlinebiblioteka.models.filters.Kategorija
import com.ets.onlinebiblioteka.models.filters.Zanr
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FcmService : FirebaseMessagingService() {
    private val NOTIFICATION_CHANNEL_ID = "OnlineLibraryNotificationChannelID"

    companion object {
        val TOPIC_NOVA_KNJIGA = "nova-knjiga"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Firebase.messaging.subscribeToTopic(TOPIC_NOVA_KNJIGA)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        createNotificationChannel()
        GlobalData.loadSharedPreferences(applicationContext)

        // show message from topic if it's been enabled in settings
        if (message.from == "/topics/$TOPIC_NOVA_KNJIGA" &&
            GlobalData.getSharedPreferences().getBoolean(TOPIC_NOVA_KNJIGA, true)) {
            val book = message.data.toBook()
            val title = book.title

            // open app and show the new book when you click the notification
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(TOPIC_NOVA_KNJIGA , book)
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            // set up notification
            val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_app)
                .setContentTitle(resources.getString(R.string.nova_knjiga))
                .setContentText("${resources.getString(R.string.knjiga_po_imenu)} $title ${resources.getString(R.string.je_dodata_u_bazu_podataka)}.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            // show notification
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(0, builder.build())
            }
        }


    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "${NOTIFICATION_CHANNEL_ID}_name"
            val descriptionText = "${NOTIFICATION_CHANNEL_ID}_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // utility function to convert the data map firebase returns to a book model
    private fun Map<String, String>.toBook(): Book {
        return Book(
            id = this["id"]!!.toInt(),
            title = this["title"]!!,
            summary = this["summary"]!!,
            authors = Gson().fromJson(this["authors"]!!, object : TypeToken<ArrayList<Autor>>() {}.type) as ArrayList<Autor>,
            categories = Gson().fromJson(this["categories"]!!, object : TypeToken<ArrayList<Kategorija>>() {}.type) as ArrayList<Kategorija>,
            genres = Gson().fromJson(this["genres"]!!, object : TypeToken<ArrayList<Zanr>>() {}.type) as ArrayList<Zanr>,
            available = this["available"]!! == "true",
            quantity = this["quantity"]!!.toInt(),
            publisher = this["publisher"]!!,
            publishYear = this["publishYear"]!!,
            photo = this["photo"]!!,
        )
    }
}