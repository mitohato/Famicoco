package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.example.mito.famicoco.MainActivity.Companion.ServerUrl
import com.example.mito.famicoco.MainActivity.Companion.tlIcon
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

class TLFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {      //タイムライン用のフラグメント

    private var adapter: CustomAdapter? = null
    private var list: ArrayList<CustomData>? = null

    @BindView(R.id.list)
    internal var listView: ListView? = null
    @BindView(R.id.swipe_tl)
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreate(saveInterfaceState: Bundle?) {
        super.onCreate(saveInterfaceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_tl, container, false)
        ButterKnife.bind(this, v)

        swipeRefreshLayout?.setColorSchemeResources(R.color.MainColorDark,
                R.color.MainColor, R.color.MainColorDark, R.color.MainColor)
        swipeRefreshLayout?.setOnRefreshListener(this)
        return v
    }

    override fun onResume() {
        super.onResume()
        UpData()
    }

    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        setHasOptionsMenu(true)

        list = ArrayList()
        adapter = CustomAdapter(context, 0, list)
    }

    fun UpData() {
        @SuppressLint("StaticFieldLeak") val task = object : AsyncTask<URL, Void, ArrayList<TLUpdateItem>>() {
            override fun doInBackground(vararg values: URL): ArrayList<TLUpdateItem> {
                var con: HttpURLConnection? = null
                var item: TLUpdateItem
                val tlUpdateItems = ArrayList<TLUpdateItem>()
                try {
                    // アクセス先URL
                    val url = values[0]
                    // ローカル処理
                    // コネクション取得
                    con = url.openConnection() as HttpURLConnection
                    con.doInput = true
                    con.connect()
                    val `in` = con.inputStream
                    val readSt = HttpGetTask.readInputStream(`in`)
                    Log.d("readString", readSt)

                    //// 配列を取得する場合
                    val jsonArray = JSONArray(readSt)

                    val m = jsonArray.length()
                    for (i in 0 until m) {
                        val data = jsonArray.getJSONObject(i)
                        item = TLUpdateItem(data)
                        tlUpdateItems.add(item)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    con?.disconnect()
                }
                return tlUpdateItems
            }

            override fun onPostExecute(tlUpdateItemArrayList: ArrayList<TLUpdateItem>) {
                list?.clear()
                adapter = CustomAdapter(context, 0, list)
                val m = tlUpdateItemArrayList.size
                for (i in 0 until m) {
                    val tlUpdateItem = tlUpdateItemArrayList[i]
                    list?.add(0, tlUpdateItem.toCustomData())
                    listView?.adapter = adapter
                }
                Log.d("hoge", "here")
            }
        }

        try {
            val url = URL(ServerUrl + "get_tl/")
            task.execute(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

    }

    override fun onRefresh() {
        Log.d("Refresh", "screen")
        UpData()
        swipeRefreshLayout!!.isRefreshing = false
    }
}

internal class TLUpdateItem @Throws(JSONException::class)
constructor(`object`: JSONObject) {
    private val text: String
    private val time: String
    private val icon: Bitmap?

    init {
        this.time = `object`.getString("time")
        this.text = `object`.getString("action")
        val type = `object`.getString("action_num")
        this.icon = tlIcon[type[0] - '0']
    }

    fun toCustomData(): CustomData {
        val customData = CustomData()
        customData.comment = this.text
        customData.setTime(this.time)
        customData.icon = this.icon
        return customData
    }
}