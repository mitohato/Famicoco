package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.example.mito.famicoco.MainActivity.Companion.ServerUrl
import org.json.JSONArray
import org.json.JSONException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.HashMap
import java.util.Timer
import java.util.TimerTask

class SotoFragment : Fragment() { // そとここ用のクラス
    @BindView(R.id.soto_listview)
    internal lateinit var sotoListView: ListView
    
    private var mHandler: Handler? = null
    private lateinit var adapter: SimpleAdapter
    private var list: ArrayList<HashMap<String, Any>> = ArrayList()
    private lateinit var iconId: IntArray
    private var data: JSONArray = JSONArray()
    
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
                R.layout.fragment_soto,
                container,
                false
        )
        ButterKnife.bind(
                this,
                view
        )
        
        return view
    }
    
    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        setHasOptionsMenu(true)
        
        // 準備
        adapter = SimpleAdapter(
                activity,
                list,
                R.layout.soto_row,
                arrayOf(
                        "iconKey0",
                        "iconKey1",
                        "iconKey2",
                        "iconKey3"
                ),
                intArrayOf(
                        R.id.icon1,
                        R.id.icon2,
                        R.id.icon3,
                        R.id.icon4
                )
        )
        
        iconId = intArrayOf(
                R.drawable.haruka,
                R.drawable.riku,
                R.drawable.father,
                R.drawable.mother,
                R.drawable.grandfather
        )
        
        sotoListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            val intent = Intent(
                    activity,
                    SotoTalkActivity::class.java
            )
            intent.putExtra(
                    "list",
                    data.toString()
            )
            intent.putExtra(
                    "select",
                    pos
            )
            startActivity(intent)
        }
    }
    
    override fun onResume() {
        super.onResume()
        mHandler = Handler()
        val mTimer = Timer()
        mTimer.schedule(
                object : TimerTask() {
                    override fun run() {
                        mHandler!!.post {
                            // 実行したい処理
                            
                            @SuppressLint("StaticFieldLeak")
                            val task = object : AsyncTask<URL, Void, JSONArray>() {
                                override fun doInBackground(vararg urls: URL): JSONArray? {
                                    val con: HttpURLConnection
                                    val readSt: String
                                    
                                    try {
                                        val url = urls[0]
                                        con = url.openConnection() as HttpURLConnection
                                        con.doInput = true
                                        con.connect()
                                        val `in` = con.inputStream
                                        readSt = HttpGetTask.readInputStream(`in`)
                                        data = JSONArray(readSt)
                                        list.clear()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    
                                    return data
                                }
                                
                                override fun onPostExecute(array: JSONArray?) {
                                    val m: Int = array?.length() ?: 0
                                    if (array == null) return
                                    list.clear()
                                    try {
                                        for (i in 1 until m) {
                                            val data = array.getJSONArray(i)
                                            val n = data.length()
                                            val item = HashMap<String, Any>()
                                            for (j in 0 until n) {
                                                var num = 0
                                                val usr = data.getString(j)
                                                when (usr) {
                                                    "しゅり" -> num = iconId[0]
                                                    "しゅうき" -> num = iconId[1]
                                                    "おとうさん" -> num = iconId[2]
                                                    "おかあさん" -> num = iconId[3]
                                                    "かんじい" -> num = iconId[4]
                                                }
                                                item["iconkey$j"] = num
                                            }
                                            list.add(item)
                                            sotoListView.adapter = adapter
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
                },
                5000,
                5000
        ) // 実行したい間隔(ミリ秒)
    }
}