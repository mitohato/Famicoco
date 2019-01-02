package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter

import org.json.JSONArray
import org.json.JSONException

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.HashMap
import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import butterknife.ButterKnife

import com.example.mito.famicoco.MainActivity.ServerUrl

class SOTOFragment : Fragment() {     //そとここ用のクラス
    @BindView(R.id.soto_listview)
    internal var soto_listView: ListView? = null

    private var mHandler: Handler? = null
    private var adapter: SimpleAdapter? = null
    private var list: ArrayList<HashMap<String, Any>>? = null
    private var iconId: IntArray? = null
    private var data: JSONArray? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_soto, container, false)
        ButterKnife.bind(this, v)

        return v
    }

    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        setHasOptionsMenu(true)
        //そとここにセットするリスト用意
        list = ArrayList()

        //準備
        adapter = SimpleAdapter(activity, list, R.layout.soto_row,
                arrayOf("iconKey0", "iconKey1", "iconKey2", "iconKey3"),
                intArrayOf(R.id.icon1, R.id.icon2, R.id.icon3, R.id.icon4))

        iconId = intArrayOf(R.drawable.haruka, R.drawable.riku, R.drawable.father, R.drawable.mother, R.drawable.grandfather)

        soto_listView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, pos, l ->
            val intent = Intent(activity, SOTOTalkActivity::class.java)
            if (data == null) return@OnItemClickListener
            intent.putExtra("list", data!!.toString())
            intent.putExtra("select", pos)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        mHandler = Handler()
        val mTimer = Timer()
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                mHandler!!.post {
                    // 実行したい処理

                    @SuppressLint("StaticFieldLeak") val task = object : AsyncTask<URL, Void, JSONArray>() {
                        override fun doInBackground(vararg urls: URL): JSONArray? {
                            val con: HttpURLConnection
                            val readSt: String
                            data = null

                            try {
                                val url = urls[0]
                                con = url.openConnection() as HttpURLConnection
                                con.doInput = true
                                con.connect()
                                val `in` = con.inputStream
                                readSt = HttpGetTask.readInputStream(`in`)
                                data = JSONArray(readSt)
                                list!!.clear()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            return data
                        }

                        override fun onPostExecute(array: JSONArray?) {
                            val m: Int
                            if (array == null) return
                            m = array.length()
                            list!!.clear()
                            try {
                                for (i in 1 until m) {
                                    val data = array.getJSONArray(i)
                                    val n = data.length()
                                    val item = HashMap<String, Any>()
                                    for (j in 0 until n) {
                                        var num = 0
                                        val usr = data.getString(j)
                                        when (usr) {
                                            "しゅり" -> num = iconId!![0]
                                            "しゅうき" -> num = iconId!![1]
                                            "おとうさん" -> num = iconId!![2]
                                            "おかあさん" -> num = iconId!![3]
                                            "かんじい" -> num = iconId!![4]
                                        }
                                        item["iconkey$j"] = num
                                    }
                                    list!!.add(item)
                                    soto_listView!!.adapter = adapter
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }
                    try {
                        val url = URL(ServerUrl + "get_out/")
                        task.execute(url)
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    }
                }
            }
        }, 5000, 5000) // 実行したい間隔(ミリ秒)
    }
}