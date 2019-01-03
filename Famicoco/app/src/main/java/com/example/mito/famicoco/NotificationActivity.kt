package com.example.mito.famicoco

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class NotificationActivity : AppCompatActivity() {
    @BindView(R.id.button2)
    internal lateinit var button: Button
    
    @OnClick(R.id.button2)
    internal fun onClickButton() {
        finish()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        ButterKnife.bind(this)
        title = "通知設定"
    }
}
