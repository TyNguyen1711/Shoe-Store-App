package com.example.shoestoreapp.classes

import android.app.AlertDialog
import android.content.Context
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun SaveInformationDialog(context: Context): Boolean {
    return suspendCoroutine { continuation ->
        val dialog = AlertDialog.Builder(context)
            .setTitle("Save Information")
            .setMessage("Do you want to save all your changes")
            .setPositiveButton("Yes") { _, _ ->
                continuation.resume(true) // Người dùng chọn "Có"
            }
            .setNegativeButton("No") { _, _ ->
                continuation.resume(false) // Người dùng chọn "Không"
            }
            .setCancelable(false)
            .create()
        dialog.show()
    }
}
