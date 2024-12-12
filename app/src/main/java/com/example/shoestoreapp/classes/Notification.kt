package com.example.shoestoreapp.classes
data class Notification(
    val title: String,
    val message: String,
    val time: String
)

data class NotificationGroup(
    val date: String,
    val notifications: List<Notification>
)