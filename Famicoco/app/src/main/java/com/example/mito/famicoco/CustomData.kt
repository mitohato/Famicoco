package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.graphics.Bitmap

import java.sql.Date
import java.text.DateFormat
import java.text.SimpleDateFormat

internal class CustomData {       //カスタムデータをセットするためのクラス
    var icon: Bitmap? = null
    var name: String? = null
    var comment: String? = null
    var time_now: String? = null
        private set

    fun setTime(time: String) {
        this.time_now = time
    }

    fun setTime() {
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("HH:mm")
        val date = Date(System.currentTimeMillis())
        this.time_now = df.format(date)
    }


}

