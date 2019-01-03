package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.example.mito.famicoco.MainActivity.Companion.ServerUrl
import com.example.mito.famicoco.MainActivity.Companion.myBeaconMacAddress
import com.example.mito.famicoco.MainActivity.Companion.myName
import com.example.mito.famicoco.MainActivity.Companion.myRegistrationId
import java.net.URL
import java.net.URLEncoder
import java.util.Timer
import java.util.TimerTask

class InitStartActivity : AppCompatActivity() {
    @BindView(R.id.name_init_setting)
    internal lateinit var editText: EditText
    @BindView(R.id.button_setting)
    internal lateinit var btn: Button
    @BindView(R.id.getBeaconID)
    internal lateinit var getBeaconID: TextView
    @BindView(R.id.getRegistrationID)
    internal lateinit var getRegistrationID: TextView
    private val isGetID = arrayOf(
            false,
            false
    )
    
    override fun onResume() {
        super.onResume()
        val mHandler = Handler()
        val mTimer = Timer()
        mTimer.schedule(
                object : TimerTask() {
                    @SuppressLint("SetTextI18n")
                    override fun run() {
                        mHandler.post {
                            // 実行したい処理
                            if (myBeaconMacAddress != "") {
                                getBeaconID.text = "OK"
                                getBeaconID.setTextColor(resources.getColor(R.color.Red))
                                isGetID[0] = true
                            }
                            if (myRegistrationId != "") {
                                getRegistrationID.text = "OK"
                                getRegistrationID.setTextColor(resources.getColor(R.color.Red))
                                isGetID[1] = true
                            }
                            if (isGetID[0] && isGetID[1]) {
                                btn.isEnabled = true
                            }
                        }
                    }
                },
                1000,
                1000
        )
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        ButterKnife.bind(this)
        title = "ふぁみここ"
        name = ""
        
        btn.setOnClickListener {
            name = editText.text.toString()
            if (name != "") {
                myName = name
                
                val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                sp
                        .edit()
                        .putString(
                                "name",
                                name
                        )
                        .apply()
                
                try {
                    name = URLEncoder.encode(
                            name,
                            "UTF-8"
                    )
                    val urlString = ServerUrl + "init_famicoco/" +
                            "?user_name=" + name +
                            "&regi_id=" + myRegistrationId +
                            "&beacon_id=" + myBeaconMacAddress
                    val url = URL(urlString)
                    HttpGetTask().execute(url)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                
                finish()
            } else if (name == "") {
                Toast.makeText(
                        this@InitStartActivity,
                        "名前を入力してください",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
        btn.isEnabled = false
    }
    
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK ->
                    // ダイアログ表示など特定の処理を行いたい場合はここに記述
                    // 親クラスのdispatchKeyEvent()を呼び出さずにtrueを返す
                    return true
            }
        }
        return super.dispatchKeyEvent(event)
    }
    
    companion object {
        var name = ""
    }
}