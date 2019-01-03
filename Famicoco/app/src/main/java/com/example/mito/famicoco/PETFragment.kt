package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.example.mito.famicoco.MainActivity.Companion.ServerUrl
import com.example.mito.famicoco.MainActivity.Companion.selectIconMapId
import com.example.mito.famicoco.MainActivity.Companion.tlIcon
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

class PETFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    
    private lateinit var adapter: CustomAdapter
    
    @BindView(R.id.pet_listview)
    internal lateinit var listView: ListView
    @BindView(R.id.swipe_pet)
    internal lateinit var swipeRefreshLayout: SwipeRefreshLayout
    
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
                R.layout.fragment_pet,
                container,
                false
        )
        ButterKnife.bind(
                this,
                view
        )
        
        swipeRefreshLayout.setColorSchemeResources(
                R.color.MainColorDark,
                R.color.MainColor, R.color.MainColorDark,
                R.color.MainColor
        )
        swipeRefreshLayout.setOnRefreshListener(this)
        return view
    }
    
    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        val list = ArrayList<CustomData>()
        adapter = CustomAdapter(
                context,
                0,
                list
        )
    }
    
    override fun onResume() {
        update()
        super.onResume()
    }
    
    private fun update() {
        selectIconMapId = HashMap()
        @SuppressLint("StaticFieldLeak")
        val task = object : AsyncTask<URL, Void, ArrayList<String>>() {
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
                    
                    // // 配列を取得する場合
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
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    con?.disconnect()
                }
                return stringArrayList
            }
            
            override fun onPostExecute(list_pet_string: ArrayList<String>) {
                val petList = ArrayList<CustomData>()
                val m = list_pet_string.size
                for (i in 0 until m) {
                    val item = CustomData()
                    item.also {
                        it.comment = list_pet_string[i]
                        it.setTime()
                        it.icon = tlIcon[5]
                    }
                    petList.add(item)
                }
                adapter = CustomAdapter(
                        context,
                        0,
                        petList
                )
                listView.adapter = adapter
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
        update()
        swipeRefreshLayout.isRefreshing = false
    }
}
