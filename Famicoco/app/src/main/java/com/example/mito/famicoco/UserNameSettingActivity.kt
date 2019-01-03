package com.example.mito.famicoco

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class UserNameSettingActivity : AppCompatActivity() {
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var relativeLayout: RelativeLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        relativeLayout = findViewById<View>(R.id.relativeLayout_setting) as RelativeLayout
        
        val btn = findViewById<View>(R.id.button_setting) as Button
        btn.setOnClickListener { finish() }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(
                relativeLayout.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
        )
        // 背景にフォーカスを移す
        relativeLayout.requestFocus()
        
        return false
    }
}
