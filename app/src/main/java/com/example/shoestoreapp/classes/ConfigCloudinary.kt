package com.example.shoestoreapp.classes

import android.app.Application
import com.cloudinary.android.MediaManager
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

class ConfigCloudinary : Application() {

    lateinit var cloudinary: Cloudinary

    override fun onCreate() {
        super.onCreate()

        val config = hashMapOf(
            "cloud_name" to "dpystprxq",
            "api_key" to "841139129692236",
            "api_secret" to "b4mjQt_ky6u99sgNCPALFImg4KU"
        )
        MediaManager.init(this, config)

        cloudinary = Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dpystprxq",
            "api_key", "841139129692236",
            "api_secret", "b4mjQt_ky6u99sgNCPALFImg4KU"
        ))
    }
}