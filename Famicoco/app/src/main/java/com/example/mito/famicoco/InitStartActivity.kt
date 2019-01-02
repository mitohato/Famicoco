package com.example.mito.famicoco

import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
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
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.util.Timer
import java.util.TimerTask

class InitStartActivity : AppCompatActivity() {
    @BindView(R.id.name_init_setting)
    internal var editText: EditText? = null
    @BindView(R.id.button_setting)
    internal var btn: Button? = null
    @BindView(R.id.getBeaconID)
    internal var getBeaconID: TextView? = null
    @BindView(R.id.getRegistrationID)
    internal var getRegistrationID: TextView? = null
    private val isGetID = arrayOf(false, false)

    override fun onResume() {
        super.onResume()
        val mHandler = Handler()
        val mTimer = Timer()
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    // 実行したい処理
                    if (myBeaconMacAddress != "") {
                        getBeaconID!!.text = "OK"
                        getBeaconID!!.setTextColor(resources.getColor(R.color.Red))
                        isGetID[0] = true
                    }
                    if (myRegistrationId != "") {
                        getRegistrationID!!.text = "OK"
                        getRegistrationID!!.setTextColor(resources.getColor(R.color.Red))
                        isGetID[1] = true
                    }
                    if (isGetID[0] === isGetID[1] && isGetID[0]) {
                        btn!!.isEnabled = true
                    }
                }
            }
        }, 1000, 1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        ButterKnife.bind(this)
        title = "ふぁみここ"
        name = ""


        btn!!.setOnClickListener {
            name = editText!!.text.toString()
            Log.d("hoge", "foo")
            if (name != "") {
                myName = name

                val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                sp.edit().putString("name", name).apply()

                try {
                    name = URLEncoder.encode(name, "UTF-8")
                    val urlString = ServerUrl + "init_famicoco/" + "?user_name=" + name + "&regi_id=" + myRegistrationId + "&beacon_id=" + myBeaconMacAddress
                    val url = URL(urlString)
                    HttpGetTask().execute(url)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                finish()
            } else if (name == "") {
                Toast.makeText(this@InitStartActivity, "名前を入力してください", Toast.LENGTH_LONG).show()
            }
        }
        btn!!.isEnabled = false
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