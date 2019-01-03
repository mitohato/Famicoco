package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.graphics.Bitmap

import java.sql.Date
import java.text.SimpleDateFormat

internal class CustomData { // カスタムデータをセットするためのクラス
    var icon: Bitmap? = null
    var name: String = ""
    var comment: String = ""
    var timeNow: String = ""
    
    fun setTime() {
        @SuppressLint("SimpleDateFormat")
        val df = SimpleDateFormat("HH:mm")
        val date = Date(System.currentTimeMillis())
        this.timeNow = df.format(date)
    }
}
