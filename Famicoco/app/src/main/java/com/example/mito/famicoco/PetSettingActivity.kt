package com.example.mito.famicoco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class PetSettingActivity : AppCompatActivity() {

    @BindView(R.id.button3)
    internal var button: Button? = null

    @OnClick(R.id.button3)
    internal fun Click() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_setting)
        ButterKnife.bind(this)

        title = "ペットここ設定"

    }
}
