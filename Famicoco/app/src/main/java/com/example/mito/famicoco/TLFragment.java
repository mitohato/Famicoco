package com.example.mito.famicoco;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mito.famicoco.MainActivity.ServerUrl;
import static com.example.mito.famicoco.MainActivity.tlIcon;

public class TLFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {      //タイムライン用のフラグメント

    private CustomAdapter adapter;
    private ArrayList<CustomData> list;

    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.swipe_tl)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle saveInterfaceState) {
        super.onCreate(saveInterfaceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tl, container, false);
        ButterKnife.bind(this, v);

        swipeRefreshLayout.setColorSchemeResources(R.color.MainColorDark,
                R.color.MainColor, R.color.MainColorDark, R.color.MainColor);
        swipeRefreshLayout.setOnRefreshListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        UpData();
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);

        list = new ArrayList<>();
        adapter = new CustomAdapter(getContext(), 0, list);
    }

    public TLFragment() {
    }

    public void UpData() {
        AsyncTask<URL, Void, ArrayList<TLUpdateItem>> task = new AsyncTask<URL, Void, ArrayList<TLUpdateItem>>() {
            @Override
            protected ArrayList<TLUpdateItem> doInBackground(URL... values) {
                HttpURLConnection con = null;
                TLUpdateItem item;
                ArrayList<TLUpdateItem> tlUpdateItems = new ArrayList<>();
                try {
                    // アクセス先URL
                    final URL url = values[0];
                    // ローカル処理
                    // コネクション取得
                    con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.connect();
                    InputStream in = con.getInputStream();
                    String readSt = HttpGetTask.readInputStream(in);
                    Log.d("readst", readSt);

                    //// 配列を取得する場合
                    JSONArray jsonArray = new JSONArray(readSt);

                    int m = jsonArray.length();
                    for (int i = 0; i < m; i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        item = new TLUpdateItem(data);
                        tlUpdateItems.add(item);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        // コネクションを切断
                        con.disconnect();
                    }
                }
                return tlUpdateItems;
            }

            @Override
            protected void onPostExecute(ArrayList<TLUpdateItem> tlUpdateItemArrayList) {
                list.clear();
                adapter = new CustomAdapter(getContext(), 0, list);
                int m = tlUpdateItemArrayList.size();
                for (int i = 0; i < m; i++) {
                    TLUpdateItem tlUpdateItem = tlUpdateItemArrayList.get(i);
                    list.add(0, tlUpdateItem.toCustomData());
                    listView.setAdapter(adapter);
                }
                Log.d("hoge", "here");
            }
        };

        try {
            URL url = new URL(ServerUrl + "get_tl/");
            task.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        Log.d("Refresh", "screen");
        UpData();
        swipeRefreshLayout.setRefreshing(false);
    }
}

class TLUpdateItem {
    private String text;
    private String time;
    private Bitmap icon;

    TLUpdateItem(JSONObject object) throws JSONException {
        this.time = object.getString("time");
        this.text = object.getString("action");
        String type = object.getString("action_num");
        this.icon = tlIcon[type.charAt(0) - '0'];
    }

    CustomData toCustomData() {
        CustomData customData = new CustomData();
        customData.setComment(this.text);
        customData.setTime(this.time);
        customData.setIcon(this.icon);
        return customData;
    }
}