package com.example.mito.famicoco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class NotificationActivity : AppCompatActivity() {
    @BindView(R.id.button2)
    internal var button: Button? = null

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
