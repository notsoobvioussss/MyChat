package com.timur.mychat

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Utils {
    companion object {
        @SuppressLint("StaticFieldLeak")
        val context = MyApplication.instance.applicationContext
        @SuppressLint("StaticFieldLeak")
        val firestore = FirebaseFirestore.getInstance()





        private val auth = FirebaseAuth.getInstance()
        private var userid: String = ""
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2

        fun getUidLoggedIn(): String {
            if (auth.currentUser != null) {
                userid = auth.currentUser!!.uid
            }
            return userid
        }
        fun getTime(): String {
            val formatter = SimpleDateFormat("yyyy:MM:dd:HH:mm:ss")
            val currentTimeUTC = System.currentTimeMillis()
            val date = Date(currentTimeUTC)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            return formatter.format(date)
        }
        fun convertToLocal(utcTime: String): String {
            val utcFormatter = SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.getDefault())
            utcFormatter.timeZone = TimeZone.getTimeZone("UTC")
            val utcDate = utcFormatter.parse(utcTime)

            val localFormatter = SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.getDefault())
            localFormatter.timeZone = TimeZone.getDefault()

            return localFormatter.format(utcDate)
        }
    }
}