package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.example.mito.famicoco.MainActivity.Companion.ServerUrl
import com.example.mito.famicoco.MainActivity.Companion.myName
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

class SotoTalkActivity : AppCompatActivity(), CustomListView.OnKeyboardAppearedListener { // そとここのリストを押したときに呼ばれるトーク画面用Activity
    
    @BindView(R.id.soto_talk_listview)
    internal lateinit var listView: CustomListView
    @BindView(R.id.button)
    internal lateinit var btn: Button
    @BindView(R.id.soto_editText)
    internal lateinit var editText: EditText
    @BindView(R.id.ie_imageView)
    internal lateinit var imageView1: ImageView
    @BindView(R.id.ie_imageView2)
    internal lateinit var imageView2: ImageView
    @BindView(R.id.ie_imageView3)
    internal lateinit var imageView3: ImageView
    @BindView(R.id.ie_imageView4)
    internal lateinit var imageView4: ImageView
    @BindView(R.id.ie_imageView5)
    internal lateinit var imageView5: ImageView
    @BindView(R.id.ie_imageView6)
    internal lateinit var imageView6: ImageView
    
    private var mTimer: Timer? = null
    private var mHandler: Handler? = null
    private lateinit var list: ArrayList<CustomData>
    private lateinit var adapter: TalkCustomAdapter
    private var str: String = ""
    private lateinit var select: JSONArray
    private lateinit var tmp: JSONArray
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sototalk)
        ButterKnife.bind(this)
        title = "そとここ"
        
        val i = intent
        val data = i.getStringExtra("list")
        val pos = i.getIntExtra("select", 0) + 1
        
        list = ArrayList()
        listView.setListener(this)
        
        imageView1.setImageResource(R.color.SubColor)
        imageView2.setImageResource(R.color.SubColor)
        imageView3.setImageResource(R.color.SubColor)
        imageView4.setImageResource(R.color.SubColor)
        imageView5.setImageResource(R.color.SubColor)
        imageView6.setImageResource(R.color.SubColor)
        try {
            val array = JSONArray(data)
            select = array.getJSONArray(pos)
            val m = select.length()
            for (j in 0 until m) {
                select.get(j)
                when (j) {
                    0 -> imageView1.setImageBitmap(IeFragment.judge(select.get(0)))
                    1 -> imageView2.setImageBitmap(IeFragment.judge(select.get(1)))
                    2 -> imageView3.setImageBitmap(IeFragment.judge(select.get(2)))
                    3 -> imageView4.setImageBitmap(IeFragment.judge(select.get(3)))
                    4 -> imageView5.setImageBitmap(IeFragment.judge(select.get(4)))
                    5 -> imageView5.setImageBitmap(IeFragment.judge(select.get(5)))
                    else -> imageView6.setImageBitmap(IeFragment.judge(select.get(6)))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        
        // 戻るボタン表示
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
        
        adapter = TalkCustomAdapter(
                this,
                0,
                list
        )
        
        btn.setOnClickListener { view ->
            str = editText.text.toString()
            val item = CustomData()
            if (str != "") {
                @SuppressLint("StaticFieldLeak")
                val task = object : AsyncTask<URL, Void, Boolean>() {
                    override fun doInBackground(vararg urls: URL): Boolean {
                        val url = urls[0]
                        val array = JSONArray()
                        val myData = JSONArray()
                        try {
                            array.put(select)
                            myData.put(myName)
                            myData.put(str)
                            array.put(1, myData)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        
                        val client = OkHttpClient()
                        
                        val body = FormBody.Builder()
                                .addEncoded(
                                        "data",
                                        array.toString()
                                )
                                .build()
                        
                        val request = Request.Builder()
                                .url(url)
                                .post(body)
                                .build()
                        
                        try {
                            val res = client.newCall(request).execute()
                            Log.d("res", res.toString())
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        
                        return true
                    }
                }
                
                try {
                    val url = URL(ServerUrl + "out/poyo/")
                    task.execute(url)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
                
                item.also {
                    it.comment = str
                    it.name = myName
                    it.setTime()
                }
                list.add(item)
                editText.setText("")
                listView.adapter = adapter
            }
            KeyboardUtils.hide(
                    applicationContext,
                    view
            )
        }
    }
    
    override fun onKeyboardAppeared() {
        
        // ListView生成済、且つサイズ変更した（キーボードが出現した）場合
        //        if (isChange) {
        
        // リストアイテムの総数-1（0番目から始まって最後のアイテム）にスクロールさせる
        val handler = Handler()
        val runnable = Runnable {
            // リストアイテムの総数-1（0番目から始まって最後のアイテム）にフォーカスさせる
            listView.smoothScrollToPosition(listView.count - 1)
        }
        handler.postDelayed(runnable, 500)
        
        //        }
    }
    
    public override fun onResume() {
        super.onResume()
        mHandler = Handler()
        mTimer = Timer()
        mTimer?.schedule(
                object : TimerTask() {
                    override fun run() {
                        mHandler?.post {
                            // 実行したい処理
                            @SuppressLint("StaticFieldLeak")
                            val task = object : AsyncTask<URL, Void, ArrayList<UpdateItem>>() {
                                override fun doInBackground(vararg urls: URL): ArrayList<UpdateItem> {
                                    val url = urls[0]
                                    val result: String
                                    
                                    val client = OkHttpClient()
                                    
                                    val body = FormBody.Builder()
                                            .add(
                                                    "data",
                                                    select.toString()
                                            )
                                            .build()
                                    val request = Request.Builder()
                                            .url(url)
                                            .get()
                                            .post(body)
                                            .build()
                                    
                                    var updateItem: UpdateItem
                                    val updateItemList = ArrayList<UpdateItem>()
                                    try {
                                        val response = client.newCall(request).execute()
                                        Log.d("response", response.toString())
                                        Log.d("response", response.body().toString())
                                        result = response.body()?.string() ?: ""
                                        
                                        Log.d("string", result)
                                        tmp = JSONArray(result)
                                        val m: Int = tmp.length()
                                        for (i in 0 until m) {
                                            val talk = tmp.getJSONObject(i)
                                            updateItem = UpdateItem(talk)
                                            updateItemList.add(updateItem)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    
                                    return updateItemList
                                }
                                
                                override fun onPostExecute(updateItemArrayList: ArrayList<UpdateItem>) {
                                    
                                    val m: Int
                                    if (list.size != updateItemArrayList.size) {
                                        list.clear()
                                        adapter = TalkCustomAdapter(
                                                applicationContext,
                                                0,
                                                list
                                        )
                                        m = updateItemArrayList.size
                                        for (i in 0 until m) {
                                            val updateItem = updateItemArrayList[i]
                                            list.add(
                                                    0,
                                                    updateItem.toCustomData()
                                            )
                                            listView.adapter = adapter
                                        }
                                        onKeyboardAppeared()
                                    }
                                }
                            }
                            try {
                                val url = URL(ServerUrl + "out/talk/")
                                task.execute(url)
                            } catch (e: MalformedURLException) {
                                e.printStackTrace()
                            }
                        }
                    }
                },
                5000,
                5000
        ) // 実行したい間隔(ミリ秒)
    }
    
    public override fun onStop() {
        super.onStop()
        mTimer?.cancel()
        mTimer = null
    }
    
    // ActionBarの「<」を押した時に元の画面に戻るように
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
