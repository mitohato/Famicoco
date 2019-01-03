package com.example.mito.famicoco

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class PetSettingActivity : AppCompatActivity() {
    
    @BindView(R.id.button3)
    internal lateinit var button: Button
    
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
