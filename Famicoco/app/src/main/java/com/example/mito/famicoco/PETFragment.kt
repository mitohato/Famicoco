package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import org.json.JSONArray
import org.json.JSONException

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

import com.example.mito.famicoco.MainActivity.ServerUrl
import com.example.mito.famicoco.MainActivity.tlIcon

class PETFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var adapter: CustomAdapter? = null

    @BindView(R.id.pet_listview)
    internal var listView: ListView? = null
    @BindView(R.id.swipe_pet)
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_pet, container, false)
        ButterKnife.bind(this, v)

        swipeRefreshLayout!!.setColorSchemeResources(R.color.MainColorDark,
                R.color.MainColor, R.color.MainColorDark, R.color.MainColor)
        swipeRefreshLayout!!.setOnRefreshListener(this)
        return v
    }

    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        val list = ArrayList<CustomData>()
        adapter = CustomAdapter(context, 0, list)
    }

    override fun onResume() {
        UpData()
        super.onResume()
    }

    fun UpData() {
        @SuppressLint("StaticFieldLeak") val task = object : AsyncTask<URL, Void, ArrayList<String>>() {
            override fun doInBackground(vararg values: URL): ArrayList<String> {
                var con: HttpURLConnection? = null
                //                ArrayList<TLUpdateItem> tlUpdateItems = new ArrayList<>();
                val stringArrayList = ArrayList<String>()
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

                    //// 配列を取得する場合
                    val jsonArray = JSONArray(readSt)

                    val m = jsonArray.length()
                    for (i in 0 until m) {
                        val text = jsonArray.getString(i)
                        stringArrayList.add(text)
                        //                        item.setComment(text);
                        //                        item.setIcon(tlIcon[5]);
                        //                        item.setTime();

                        //                        item = new TLUpdateItem(data);
                        //                        tlUpdateItems.add(item);
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    con?.disconnect()
                }
                return stringArrayList
            }

            override fun onPostExecute(list_pet_string: ArrayList<String>) {
                val list_pet = ArrayList<CustomData>()
                val m = list_pet_string.size
                for (i in 0 until m) {
                    val item = CustomData()
                    item.comment = list_pet_string[i]
                    item.setTime()
                    item.icon = tlIcon[5]
                    list_pet.add(item)
                }
                adapter = CustomAdapter(context, 0, list_pet)
                listView!!.adapter = adapter
                Log.d("hoge", "here")
            }
        }
        try {
            val url = URL(ServerUrl + "get_pet/")
            task.execute(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

    }

    override fun onRefresh() {
        UpData()
        swipeRefreshLayout!!.isRefreshing = false
    }
}
