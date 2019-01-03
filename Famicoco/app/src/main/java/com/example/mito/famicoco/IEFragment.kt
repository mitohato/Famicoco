package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.example.mito.famicoco.MainActivity.Companion.ServerUrl
import com.example.mito.famicoco.MainActivity.Companion.myName
import com.example.mito.famicoco.MainActivity.Companion.selectIconMap
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

class IEFragment : Fragment(), CustomListView.OnKeyboardAppearedListener { // いえここ用のフラグメント
    private var adapter: TalkCustomAdapter? = null
    private var mTimer: Timer? = null
    private var mHandler: Handler? = null
    private var list: ArrayList<CustomData> = ArrayList()
    private val ieList: ArrayList<Any> = ArrayList()
    
    @BindView(R.id.ie_listView)
    internal lateinit var ieListView: CustomListView
    @BindView(R.id.editText)
    internal lateinit var editText: EditText
    @BindView(R.id.button)
    internal lateinit var button: Button
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
    
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
                R.layout.fragment_ie,
                container,
                false
        )
        ButterKnife.bind(
                this,
                view
        )
        ieListView.setListener(this)
        return view
    }
    
    override fun onResume() {
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
                                    
                                    val con: HttpURLConnection
                                    val readSt: String
                                    
                                    var updateItem: UpdateItem
                                    val updateItemList = ArrayList<UpdateItem>()
                                    try {
                                        val url = urls[0]
                                        con = url.openConnection() as HttpURLConnection
                                        con.doInput = true
                                        con.connect()
                                        
                                        val `in` = con.inputStream
                                        readSt = HttpGetTask.readInputStream(`in`)
                                        
                                        val data = JSONArray(readSt)
                                        val member = data.getJSONArray(0)
                                        ieList.clear()
                                        var m = member.length()
                                        for (i in 0 until m) {
                                            ieList.add(member.get(i))
                                        }
                                        
                                        val talks = data.getJSONArray(1)
                                        m = talks.length()
                                        for (i in 0 until m) {
                                            val talk = talks.getJSONObject(i)
                                            updateItem = UpdateItem(talk)
                                            updateItemList.add(updateItem)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    
                                    return updateItemList
                                }
                                
                                override fun onPostExecute(updateItemArrayList: ArrayList<UpdateItem>) {
                                    var m = ieList.size
                                    imageView1.setImageResource(R.color.SubColor)
                                    imageView2.setImageResource(R.color.SubColor)
                                    imageView3.setImageResource(R.color.SubColor)
                                    imageView4.setImageResource(R.color.SubColor)
                                    imageView5.setImageResource(R.color.SubColor)
                                    imageView6.setImageResource(R.color.SubColor)
                                    for (i in 0 until m) {
                                        when (i) {
                                            0 -> imageView1.setImageBitmap(judge(ieList[0]))
                                            1 -> imageView2.setImageBitmap(judge(ieList[1]))
                                            2 -> imageView3.setImageBitmap(judge(ieList[2]))
                                            3 -> imageView4.setImageBitmap(judge(ieList[3]))
                                            4 -> imageView5.setImageBitmap(judge(ieList[4]))
                                            5 -> imageView6.setImageBitmap(judge(ieList[5]))
                                        }
                                    }
                                    if (list.size != updateItemArrayList.size) {
                                        list.clear()
                                        adapter = TalkCustomAdapter(context, 0, list)
                                        m = updateItemArrayList.size
                                        for (i in 0 until m) {
                                            val updateItem = updateItemArrayList[i]
                                            list.add(0, updateItem.toCustomData())
                                            ieListView.adapter = adapter
                                        }
                                        onKeyboardAppeared()
                                    }
                                }
                            }
                            try {
                                val url = URL(ServerUrl + "home/")
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        icon[0] = BitmapFactory.decodeResource(
                resources,
                R.drawable.haruka
        )
        icon[1] = BitmapFactory.decodeResource(
                resources,
                R.drawable.riku
        )
        icon[2] = BitmapFactory.decodeResource(
                resources,
                R.drawable.mother
        )
        icon[3] = BitmapFactory.decodeResource(
                resources,
                R.drawable.father
        )
        icon[4] = BitmapFactory.decodeResource(
                resources,
                R.drawable.grandfather
        )
    }
    
    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        setHasOptionsMenu(true)
        
        list = ArrayList()
        
        // いえここの送るボタンに機能を設定
        button.setOnClickListener {
            var str = editText.text.toString()
            val item = CustomData()
            adapter = TalkCustomAdapter(
                    context,
                    0,
                    list
            )
            
            if (str != "") {
                val text = str
                try {
                    str = URLEncoder.encode(
                            str,
                            "UTF-8"
                    )
                    val name = URLEncoder.encode(
                            myName,
                            "UTF-8"
                    )
                    val urlString = ServerUrl + "home/talk/?name=" + name + "&text=" + str
                    val url = URL(urlString)
                    HttpGetTask().execute(url)
                } catch (e: MalformedURLException) {
                    Toast.makeText(context, "connection failed", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                } catch (e: UnsupportedEncodingException) {
                    Toast.makeText(context, "Encoding failed", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
                
                item.comment = text
                item.name = myName
                item.setTime()
                list.add(item)
                editText.setText("")
                ieListView.adapter = adapter
                onKeyboardAppeared()
            }
            activity?.let { activity -> KeyboardUtils.hide(activity) }
        }
    }
    
    override fun onStop() {
        super.onStop()
        if (mTimer != null) {
            mTimer?.cancel()
            mTimer = null
        }
    }
    
    override fun onKeyboardAppeared() {
        
        // ListView生成済、且つサイズ変更した（キーボードが出現した）場合
        //        if (isChange) {
        // スクロールアニメーションが要らない場合はこれでOK
        ieListView.setSelection(ieListView.count - 1)
        //        }
    }
    
    companion object {
        var icon = ArrayList<Bitmap>()
        
        fun judge(s: Any): Bitmap? {
            return when (s) {
                "しゅり" -> icon[0]
                "しゅうき" -> icon[1]
                "おかあさん" -> icon[2]
                "おとうさん" -> icon[3]
                "かんじい" -> icon[4]
                else -> selectIconMap[s]
            }
        }
    }
}

internal class UpdateItem @Throws(JSONException::class)
constructor(json: JSONObject) {
    private val name: String = json.getString("name")
    private val comment: String = json.getString("text")
    private val time: String = json.getString("time")
    private var icon: Bitmap? = null
    
    init {
        when (name) {
            "しゅり" -> this.icon = IEFragment.icon[0]
            "しゅうき" -> this.icon = IEFragment.icon[1]
            "お母さん" -> this.icon = IEFragment.icon[2]
            "お父さん" -> this.icon = IEFragment.icon[3]
            "かん爺" -> this.icon = IEFragment.icon[4]
            else -> this.icon = IEFragment.icon[5]
        }
    }
    
    fun toCustomData(): CustomData {
        val customData = CustomData()
        customData.also {
            it.timeNow = this.time
            it.comment = this.comment
            it.name = this.name
            // ここにif文を書く
            it.icon = this.icon
        }
        return customData
    }
}