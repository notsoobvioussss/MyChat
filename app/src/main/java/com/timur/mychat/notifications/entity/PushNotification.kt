package com.timur.mychat.notifications.entity

data class PushNotification(
    val data: NotificationData,
    val to: String
)