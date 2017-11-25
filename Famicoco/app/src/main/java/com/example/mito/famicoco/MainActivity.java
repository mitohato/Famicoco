package com.example.mito.famicoco;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.mito.famicoco.IBeaconScanCallback.beaconId;
import static com.example.mito.famicoco.IBeaconScanCallback.f_init;

public class MainActivity extends AppCompatActivity {

    BluetoothLeScanner mBluetoothLeScanner;
    public static String myBeaconMacAddress = "";
    IBeaconScanCallback mScanCallback;
    public static final String ServerUrl = "SERVERURL";
    public static String myName = "";
    public static Bitmap tlIcon[] = new Bitmap[6];
    public static String myRegistrationId = "";
    private Handler mHandler = null;
    public static HashMap<String, Bitmap> selectIcon_map = null;
    public static HashMap<String, Integer> selectIcon_map_id = null;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final BluetoothManager bManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        assert bManager != null;
        final BluetoothAdapter bluetoothAdapter = bManager.getAdapter();

        setTitle(getString(R.string.tl));

        selectIcon_map = new HashMap<>();
        selectIcon_map_id = new HashMap<>();
        tlIcon[0] = BitmapFactory.decodeResource(getResources(), R.drawable.setting);
        tlIcon[1] = BitmapFactory.decodeResource(getResources(), R.drawable.home);
        tlIcon[2] = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        tlIcon[3] = BitmapFactory.decodeResource(getResources(), R.drawable.humen);
        tlIcon[4] = tlIcon[3];
        tlIcon[5] = BitmapFactory.decodeResource(getResources(), R.drawable.pet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                KeyboardUtils.hide(MainActivity.this);
                if (position == 0)
                    setTitle(getString(R.string.tl));
                else if (position == 1)
                    setTitle(getString(R.string.ie));
                else if (position == 2)
                    setTitle(getString(R.string.soto));
                else
                    setTitle(getString(R.string.pet));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabLayout.setupWithViewPager(mViewPager);

        TabLayout.Tab tab;
        int[] drawableId = new int[]{R.drawable.tab1_selector, R.drawable.tab2_selector, R.drawable.tab3_selector, R.drawable.tab4_selector};
        for (int i = 0; i < 4; i++) {
            tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setIcon(drawableId[i]);
        }

        bluetoothAdapter.enable(); //OK

        if (!bluetoothAdapter.isEnabled())
            new AlertDialog.Builder(this)
                    .setTitle("確認")
                    .setMessage("Bluetooth機能をONにしました")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();

        while (!bluetoothAdapter.isEnabled()) ;

        // BLEScan開始 not Estimote sdk
        mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        mScanCallback = new IBeaconScanCallback();
        mBluetoothLeScanner.startScan(mScanCallback);

        Log.d("before", "log");
        if (AppLaunchChecker.hasStartedFromLauncher(this)) {
            Log.d("AppLaunchChecker", "2回目以降");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            myName = sp.getString("name", null);
            myBeaconMacAddress = sp.getString("myBeacon", null);
            f_init = false;
        } else {
            Log.d("AppLaunchChecker", "はじめてアプリを起動した");
            Intent intent = new Intent(this, InitStartActivity.class);
            startActivity(intent);
            AppLaunchChecker.onActivityCreate(this);
        }
        connect();
    }

    public void connect() {
        mHandler = new Handler();
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 実行したい処理
                        @SuppressLint("StaticFieldLeak") AsyncTask<URL, Void, Boolean> asyncTask = new AsyncTask<URL, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(URL... urls) {
                                final URL url = urls[0];
                                HttpURLConnection con;

                                try {
                                    con = (HttpURLConnection) url.openConnection();
                                    con.setDoInput(true);
                                    Log.d("TAG", "Connected");
                                    con.connect();
                                    InputStream in = con.getInputStream();
                                    String readSt = HttpGetTask.readInputStream(in);

                                    //// 配列を取得する場合
                                    JSONArray jsonArray = new JSONArray(readSt);
                                    int m = jsonArray.length();
                                    for (int i = 0; i < m; i++) {
                                        JSONArray array = jsonArray.getJSONArray(i);
                                        String num = array.getString(1);
                                        String str = array.getString(0);
                                        selectIcon_map_id.put(str, num.charAt(0) - '0');
                                    }

                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                        };

                        try {
                            URL url = new URL(ServerUrl + "user/pict/");
                            asyncTask.execute(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        @SuppressLint("StaticFieldLeak") AsyncTask<URL, Void, Boolean> task = new AsyncTask<URL, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(URL... urls) {
                                final URL url = urls[0];

                                OkHttpClient client = new OkHttpClient();
                                JSONArray array = new JSONArray();
                                for (int i = 0; i < beaconId.size(); i++) {
                                    array.put(beaconId.get(i));
                                }

                                RequestBody body = new FormBody.Builder()
                                        .add("data", array.toString())
                                        .add("sender", myName)
                                        .build();
                                Request request = new Request.Builder()
                                        .url(url)
                                        .post(body)
                                        .build();

                                try {
                                    client.newCall(request).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                beaconId.clear();
                                return true;
                            }
                        };

                        try {
                            URL url = new URL(ServerUrl + "receive_beacon/");
                            task.execute(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 5000, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.notification_setting) {
            intent = new Intent(getApplicationContext(), NotificationActivity.class);
        } else if (id == R.id.pet_setting) {
            intent = new Intent(getApplicationContext(), PetSettingActivity.class);
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //タブのフラグメントを渡す
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new TLFragment();
                case 1:
                    return new IEFragment();
                case 2:
                    return new SOTOFragment();
                case 3:
                    return new PETFragment();
            }
            return null;
        }

        //タブの数
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        //タブの名前
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}