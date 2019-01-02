package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import butterknife.ButterKnife
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

import com.example.mito.famicoco.MainActivity.ServerUrl
import com.example.mito.famicoco.MainActivity.myName

class SOTOTalkActivity : AppCompatActivity(), CustomListView.OnKeyboardAppearedListener { //そとここのリストを押したときに呼ばれるトーク画面用Activity

    @BindView(R.id.soto_talk_listview)
    internal var listView: CustomListView? = null
    @BindView(R.id.button)
    internal var btn: Button? = null
    @BindView(R.id.soto_editText)
    internal var editText: EditText? = null
    @BindView(R.id.ie_imageView)
    internal var imageView1: ImageView? = null
    @BindView(R.id.ie_imageView2)
    internal var imageView2: ImageView? = null
    @BindView(R.id.ie_imageView3)
    internal var imageView3: ImageView? = null
    @BindView(R.id.ie_imageView4)
    internal var imageView4: ImageView? = null
    @BindView(R.id.ie_imageView5)
    internal var imageView5: ImageView? = null
    @BindView(R.id.ie_imageView6)
    internal var imageView6: ImageView? = null

    private var mTimer: Timer? = null
    private var mHandler: Handler? = null
    private var list: ArrayList<CustomData>? = null
    private var adapter: TalkCustomAdapter? = null
    private var str: String? = null
    private var select: JSONArray? = null
    private var tmp: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sototalk)
        ButterKnife.bind(this)
        title = "そとここ"

        val i = intent
        val data = i.getStringExtra("list")
        val pos = i.getIntExtra("select", 0) + 1

        list = ArrayList()
        listView!!.setListener(this)

        imageView1!!.setImageResource(R.color.SubColor)
        imageView2!!.setImageResource(R.color.SubColor)
        imageView3!!.setImageResource(R.color.SubColor)
        imageView4!!.setImageResource(R.color.SubColor)
        imageView5!!.setImageResource(R.color.SubColor)
        imageView6!!.setImageResource(R.color.SubColor)
        try {
            val array = JSONArray(data)
            select = array.getJSONArray(pos)
            val m = select!!.length()
            for (j in 0 until m) {
                select!!.get(j)
                if (j == 0)
                    imageView1!!.setImageBitmap(IEFragment.judge(select!!.get(0)))
                else if (j == 1)
                    imageView2!!.setImageBitmap(IEFragment.judge(select!!.get(1)))
                else if (j == 2)
                    imageView3!!.setImageBitmap(IEFragment.judge(select!!.get(2)))
                else if (j == 3)
                    imageView4!!.setImageBitmap(IEFragment.judge(select!!.get(3)))
                else if (j == 4)
                    imageView5!!.setImageBitmap(IEFragment.judge(select!!.get(4)))
                else if (j == 5)
                    imageView5!!.setImageBitmap(IEFragment.judge(select!!.get(5)))
                else
                    imageView6!!.setImageBitmap(IEFragment.judge(select!!.get(6)))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        //戻るボタン表示
        val actionbar = supportActionBar!!
        actionbar.setHomeButtonEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        adapter = TalkCustomAdapter(this, 0, list)

        btn!!.setOnClickListener { view ->
            str = editText!!.text.toString()
            val item = CustomData()
            if (str != "") {
                @SuppressLint("StaticFieldLeak") val task = object : AsyncTask<URL, Void, Boolean>() {
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
                                .addEncoded("data", array.toString())
                                .build()

                        val request = Request.Builder()
                                .url(url)
                                .post(body)
                                .build()

                        Log.d("string", request.toString())
                        Log.d("string", select!!.toString())
                        Log.d("string", body.toString())
                        Log.d("string", array.toString())
                        try {
                            val res = client.newCall(request).execute()
                            if (res != null) Log.d("res", res.toString())
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

                item.comment = str
                item.name = myName
                item.setTime()
                list!!.add(item)
                editText!!.setText("")
                listView!!.adapter = adapter
            }
            KeyboardUtils.hide(applicationContext, view)
        }

    }

    override fun onKeyboardAppeared() {

        //ListView生成済、且つサイズ変更した（キーボードが出現した）場合
        //        if (isChange) {

        //リストアイテムの総数-1（0番目から始まって最後のアイテム）にスクロールさせる
        val handler = Handler()
        val runnable = Runnable {
            //リストアイテムの総数-1（0番目から始まって最後のアイテム）にフォーカスさせる
            listView!!.smoothScrollToPosition(listView!!.count - 1)
        }
        handler.postDelayed(runnable, 500)

        //        }
    }

    public override fun onResume() {
        super.onResume()
        mHandler = Handler()
        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler!!.post {
                    // 実行したい処理
                    @SuppressLint("StaticFieldLeak") val task = object : AsyncTask<URL, Void, ArrayList<UpdateItem>>() {
                        override fun doInBackground(vararg urls: URL): ArrayList<UpdateItem> {
                            val url = urls[0]
                            val result: String

                            val client = OkHttpClient()

                            val body = FormBody.Builder()
                                    .add("data", select!!.toString())
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
                                Log.d("response", response.body()!!.toString())
                                result = response.body()!!.string()

                                Log.d("string", result)
                                tmp = JSONArray(result)
                                val m: Int
                                m = tmp!!.length()
                                for (i in 0 until m) {
                                    val talk = tmp!!.getJSONObject(i)
                                    updateItem = UpdateItem(talk)
                                    updateItemList.add(updateItem)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }


                            return updateItemList
                        }

                        override fun onPostExecute(updateItemArrayList: ArrayList<UpdateItem>) {

                            val m: Int
                            if (list!!.size != updateItemArrayList.size) {
                                list!!.clear()
                                adapter = TalkCustomAdapter(applicationContext, 0, list)
                                m = updateItemArrayList.size
                                for (i in 0 until m) {
                                    val updateItem = updateItemArrayList[i]
                                    list!!.add(0, updateItem.toCustomData())
                                    listView!!.adapter = adapter
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
        }, 5000, 5000) // 実行したい間隔(ミリ秒)
    }

    public override fun onStop() {
        super.onStop()
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    // ActionBarの「<」を押した時に元の画面に戻るように
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
}
