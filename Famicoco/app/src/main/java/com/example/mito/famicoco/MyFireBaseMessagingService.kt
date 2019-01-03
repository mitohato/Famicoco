/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mito.famicoco

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFireBaseMessagingService : FirebaseMessagingService() {
    // private static final String action =
    private var data: Map<*, *>? = null
    
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val from = remoteMessage!!.from // センダーIDを受け取る感じのやつ
        data = remoteMessage.data // ペイロードデータ
        Log.d("map", data!!.toString())
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
        
        // フォアグラウンドにいるときもメッセージを受け取る
        super.onMessageReceived(remoteMessage)
        //        Log.d(TAG, remoteMessage.getNotification().getBody());
        Log.d("map_body", data!!["body"] as String?)
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + from!!)
        
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + data!!)
        }
        
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }
        // ここで通知受け取った際の処理を書けばよさそう(ポップアップ等)
        //        String text = String.valueOf(data.get("body"));
        //        String title = String.valueOf(data.get("title"));
        // nemuが書いた。
        // Map<String, String> data = remoteMessage.getData();
        //        String ContentId = (String) data.get("nemu");
        //        Log.d(TAG, "onMessageReceived id: " + ContentId);
        Log.d("im", "here")
        notificationManager.notify(
                1,
                createNotification(true)
        )
        //        Intent intent = new Intent(getApplicationContext(), ShowDiaLog.class);
        //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        intent.putExtra("title", title);
        //        intent.putExtra("text", text);
        //        startActivity(intent);
        
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]
    
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("ふぁみここ")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build())
    }
    
    private fun createNotification(makeHeadsUpNotification: Boolean): Notification {
        val notificationBuilder = Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle(data!!["title"].toString())
                .setContentText("This is a normal notification.")
        if (makeHeadsUpNotification) {
            val push = Intent()
            push.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            push.setClass(
                    this,
                    TLFragment::class.java
            )
            
            val fullScreenPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    push,
                    PendingIntent.FLAG_CANCEL_CURRENT
            )
            notificationBuilder
                    .setContentText(data!!["body"].toString())
                    .setFullScreenIntent(
                            fullScreenPendingIntent,
                            true
                    )
        }
        return notificationBuilder.build()
    }
    
    companion object {
        private val TAG = "Famicoco"
    }
}
