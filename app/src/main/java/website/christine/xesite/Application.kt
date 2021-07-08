/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package website.christine.xesite

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        val ctx = this.applicationContext
        createNotificationChannel(ctx)
    }

    private fun createNotificationChannel(ctx: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NEW_POST_CHANNEL
            val descriptionText = NEW_POST_CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NEW_POST_CHANNEL, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

internal val NEW_POST_CHANNEL = "New Posts";
internal val NEW_POST_CHANNEL_DESC = "New posts on christine.website";