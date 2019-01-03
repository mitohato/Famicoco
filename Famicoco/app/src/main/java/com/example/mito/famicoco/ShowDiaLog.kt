package com.example.mito.famicoco

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ShowDiaLog : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_dia_log)
        
        val i = intent
        val text = i.getStringExtra("text")
        val title = i.getStringExtra("title")
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(text)
        alertBuilder.setPositiveButton("閉じる") { _, _ ->
            this@ShowDiaLog.finish() // 選択をしたら自信のActivityを終了させる
        }
        alertBuilder.create().show()
    }
}
