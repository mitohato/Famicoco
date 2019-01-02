package com.example.mito.famicoco;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

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

public class PETFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private CustomAdapter adapter;

    @BindView(R.id.pet_listview)
    ListView listView;
    @BindView(R.id.swipe_pet)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pet, container, false);
        ButterKnife.bind(this, v);

        swipeRefreshLayout.setColorSchemeResources(R.color.MainColorDark,
                R.color.MainColor, R.color.MainColorDark, R.color.MainColor);
        swipeRefreshLayout.setOnRefreshListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ArrayList<CustomData> list = new ArrayList<>();
        adapter = new CustomAdapter(getContext(), 0, list);
    }

    public PETFragment() {
    }

    @Override
    public void onResume() {
        UpData();
        super.onResume();
    }

    public void UpData() {
        @SuppressLint("StaticFieldLeak") AsyncTask<URL, Void, ArrayList<String>> task = new AsyncTask<URL, Void, ArrayList<String>>() {
            @Override
            protected ArrayList<String> doInBackground(URL... values) {
                HttpURLConnection con = null;
//                ArrayList<TLUpdateItem> tlUpdateItems = new ArrayList<>();
                ArrayList<String> stringArrayList = new ArrayList<>();
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

                    //// 配列を取得する場合
                    JSONArray jsonArray = new JSONArray(readSt);

                    int m = jsonArray.length();
                    for (int i = 0; i < m; i++) {
                        String text = jsonArray.getString(i);
                        stringArrayList.add(text);
//                        item.setComment(text);
//                        item.setIcon(tlIcon[5]);
//                        item.setTime();

//                        item = new TLUpdateItem(data);
//                        tlUpdateItems.add(item);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        // コネクションを切断
                        con.disconnect();
                    }
                }
                return stringArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<String> list_pet_string) {
                ArrayList<CustomData> list_pet = new ArrayList<>();
                int m = list_pet_string.size();
                for (int i = 0; i < m; i++) {
                    CustomData item = new CustomData();
                    item.setComment(list_pet_string.get(i));
                    item.setTime();
                    item.setIcon(tlIcon[5]);
                    list_pet.add(item);
                }
                adapter = new CustomAdapter(getContext(), 0, list_pet);
                listView.setAdapter(adapter);
                Log.d("hoge", "here");
            }
        };
        try {
            URL url = new URL(ServerUrl + "get_pet/");
            task.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        UpData();
        swipeRefreshLayout.setRefreshing(false);
    }
}
