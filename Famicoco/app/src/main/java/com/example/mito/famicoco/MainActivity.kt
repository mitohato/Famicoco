package com.example.mito.famicoco

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.AppLaunchChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.example.mito.famicoco.IBeaconScanCallback.Companion.beaconIds
import com.example.mito.famicoco.IBeaconScanCallback.Companion.f_init
import com.google.android.material.tabs.TabLayout
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.HashMap
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    
    private lateinit var mBluetoothLeScanner: BluetoothLeScanner
    private lateinit var mScanCallback: IBeaconScanCallback
    private var mHandler: Handler? = null
    
    @BindView(R.id.tabs)
    internal lateinit var tabLayout: TabLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        
        val bManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bManager.adapter
        
        title = getString(R.string.tl)
        
        tlIcon[0] = BitmapFactory.decodeResource(
                resources,
                R.drawable.setting
        )
        tlIcon[1] = BitmapFactory.decodeResource(
                resources,
                R.drawable.home
        )
        tlIcon[2] = BitmapFactory.decodeResource(
                resources,
                R.drawable.car
        )
        tlIcon[3] = BitmapFactory.decodeResource(
                resources,
                R.drawable.humen
        )
        tlIcon[4] = tlIcon[3]
        tlIcon[5] = BitmapFactory.decodeResource(
                resources,
                R.drawable.pet
        )
        
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        val mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        
        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        val mViewPager = findViewById<View>(R.id.container) as ViewPager
        mViewPager.adapter = mSectionsPagerAdapter
        
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
            }
            
            override fun onPageSelected(position: Int) {
                KeyboardUtils.hide(this@MainActivity)
                title = when (position) {
                    0 -> getString(R.string.tl)
                    1 -> getString(R.string.ie)
                    2 -> getString(R.string.soto)
                    else -> getString(R.string.pet)
                }
            }
            
            override fun onPageScrollStateChanged(state: Int) {}
        })
        
        tabLayout.setupWithViewPager(mViewPager)
        
        var tab: TabLayout.Tab?
        val drawableId = intArrayOf(
                R.drawable.tab1_selector,
                R.drawable.tab2_selector,
                R.drawable.tab3_selector,
                R.drawable.tab4_selector
        )
        for (i in 0..3) {
            tab = tabLayout.getTabAt(i)
            assert(tab != null)
            tab!!.setIcon(drawableId[i])
        }
        
        bluetoothAdapter.enable() // OK
        
        if (!bluetoothAdapter.isEnabled)
            AlertDialog.Builder(this)
                    .setTitle("確認")
                    .setMessage("Bluetooth機能をONにしました")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
        
        while (!bluetoothAdapter.isEnabled)
        
        // BLEScan開始 not Estimote sdk
            mBluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        mScanCallback = IBeaconScanCallback()
        mBluetoothLeScanner.startScan(mScanCallback)
        
        if (AppLaunchChecker.hasStartedFromLauncher(this)) {
            Log.d("AppLaunchChecker", "2回目以降")
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            myName = sp.getString(
                    "name",
                    null
            ) ?: ""
            myBeaconMacAddress = sp.getString(
                    "myBeacon",
                    null
            ) ?: ""
            f_init = false
        } else {
            Log.d("AppLaunchChecker", "はじめてアプリを起動した")
            val intent = Intent(
                    this,
                    InitStartActivity::class.java
            )
            startActivity(intent)
            AppLaunchChecker.onActivityCreate(this)
        }
        connect()
    }
    
    private fun connect() {
        mHandler = Handler()
        val mTimer = Timer()
        mTimer.schedule(
                object : TimerTask() {
                    override fun run() {
                        mHandler!!.post {
                            // 実行したい処理
                            @SuppressLint("StaticFieldLeak")
                            val asyncTask = object : AsyncTask<URL, Void, Boolean>() {
                                override fun doInBackground(vararg urls: URL): Boolean {
                                    val url = urls[0]
                                    val con: HttpURLConnection
                                    
                                    try {
                                        con = url.openConnection() as HttpURLConnection
                                        con.doInput = true
                                        con.connect()
                                        val `in` = con.inputStream
                                        val readSt = HttpGetTask.readInputStream(`in`)
                                        
                                        // // 配列を取得する場合
                                        val jsonArray = JSONArray(readSt)
                                        val m = jsonArray.length()
                                        for (i in 0 until m) {
                                            val array = jsonArray.getJSONArray(i)
                                            val num = array.getString(1)
                                            val str = array.getString(0)
                                            selectIconMapId[str] = num[0] - '0'
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    
                                    return true
                                }
                            }
                            
                            try {
                                val url = URL(ServerUrl + "user/pict/")
                                asyncTask.execute(url)
                            } catch (e: MalformedURLException) {
                                e.printStackTrace()
                            }
                            
                            @SuppressLint("StaticFieldLeak")
                            val task = object : AsyncTask<URL, Void, Boolean>() {
                                override fun doInBackground(vararg urls: URL): Boolean {
                                    val url = urls[0]
                                    
                                    val client = OkHttpClient()
                                    val array = JSONArray()
                                    for (i in beaconIds.indices) {
                                        array.put(beaconIds[i])
                                    }
                                    
                                    val body = FormBody.Builder()
                                            .add(
                                                    "data",
                                                    array.toString()
                                            )
                                            .add(
                                                    "sender",
                                                    myName
                                            )
                                            .build()
                                    val request = Request.Builder()
                                            .url(url)
                                            .post(body)
                                            .build()
                                    
                                    try {
                                        client.newCall(request).execute()
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    
                                    beaconIds.clear()
                                    return true
                                }
                            }
                            
                            try {
                                val url = URL(ServerUrl + "receive_beacon/")
                                task.execute(url)
                            } catch (e: MalformedURLException) {
                                e.printStackTrace()
                            }
                        }
                    }
                },
                5000,
                5000
        )
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(
                R.menu.menu_main,
                menu
        )
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        
        var intent: Intent? = null
        when (id) {
            R.id.notification_setting -> intent = Intent(
                    applicationContext,
                    NotificationActivity::class.java
            )
            R.id.pet_setting -> intent = Intent(
                    applicationContext,
                    PetSettingActivity::class.java
            )
        }
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }
    
    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {
        
        override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(
                    R.layout.fragment_main,
                    container,
                    false
            )
            val textView = rootView.findViewById<View>(R.id.section_label) as TextView
            textView.text = getString(R.string.section_format, arguments!!.getInt(ARG_SECTION_NUMBER))
            return rootView
        }
        
        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private const val ARG_SECTION_NUMBER = "section_number"
        }
    }
    
    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        
        // タブのフラグメントを渡す
        override fun getItem(position: Int): Fragment? {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            when (position) {
                0 -> return TLFragment()
                1 -> return IeFragment()
                2 -> return SotoFragment()
                3 -> return PetFragment()
            }
            return null
        }
        
        // タブの数
        override fun getCount(): Int {
            // Show 3 total pages.
            return 4
        }
        
        // タブの名前
        override fun getPageTitle(position: Int): CharSequence? {
            return null
        }
    }
    
    companion object {
        var myBeaconMacAddress: String = ""
        const val ServerUrl = "SERVERURL"
        var myName: String = ""
        var tlIcon = ArrayList<Bitmap>(6)
        var myRegistrationId = ""
        var selectIconMap: HashMap<String, Bitmap> = HashMap()
        var selectIconMapId: HashMap<String, Int> = HashMap()
    }
}