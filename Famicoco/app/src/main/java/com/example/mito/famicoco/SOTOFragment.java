package com.example.mito.famicoco;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mito.famicoco.MainActivity.ServerUrl;

public class SOTOFragment extends Fragment {     //そとここ用のクラス
    @BindView(R.id.soto_listview)
    ListView soto_listView;

    private Handler mHandler = null;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, Object>> list;
    private int iconId[];
    private JSONArray data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_soto, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);
        //そとここにセットするリスト用意
        list = new ArrayList<>();

        //準備
        adapter = new SimpleAdapter(getActivity(), list, R.layout.soto_row,
                new String[]{"iconKey0", "iconKey1", "iconKey2", "iconKey3"},
                new int[]{R.id.icon1, R.id.icon2, R.id.icon3, R.id.icon4});

        iconId = new int[]{R.drawable.haruka, R.drawable.riku, R.drawable.father, R.drawable.mother, R.drawable.grandfather};

        soto_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent intent = new Intent(getActivity(), SOTOTalkActivity.class);
                if (data == null) return;
                intent.putExtra("list", data.toString());
                intent.putExtra("select", pos);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler = new Handler();
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 実行したい処理

                        @SuppressLint("StaticFieldLeak") AsyncTask<URL, Void, JSONArray> task = new AsyncTask<URL, Void, JSONArray>() {
                            @Override
                            protected JSONArray doInBackground(URL... urls) {
                                HttpURLConnection con;
                                String readSt;
                                data = null;

                                try {
                                    final URL url = urls[0];
                                    con = (HttpURLConnection) url.openConnection();
                                    con.setDoInput(true);
                                    con.connect();
                                    InputStream in = con.getInputStream();
                                    readSt = HttpGetTask.readInputStream(in);
                                    data = new JSONArray(readSt);
                                    list.clear();
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                                return data;
                            }

                            @Override
                            protected void onPostExecute(JSONArray array) {
                                int m;
                                if (array == null) return;
                                m = array.length();
                                list.clear();
                                try {
                                    for (int i = 1; i < m; i++) {
                                        JSONArray data = array.getJSONArray(i);
                                        int n = data.length();
                                        HashMap<String, Object> item = new HashMap<>();
                                        for (int j = 0; j < n; j++) {
                                            int num = 0;
                                            String usr = data.getString(j);
                                            switch (usr) {
                                                case "しゅり":
                                                    num = iconId[0];
                                                    break;
                                                case "しゅうき":
                                                    num = iconId[1];
                                                    break;
                                                case "おとうさん":
                                                    num = iconId[2];
                                                    break;
                                                case "おかあさん":
                                                    num = iconId[3];
                                                    break;
                                                case "かんじい":
                                                    num = iconId[4];
                                                    break;
                                            }
                                            item.put(String.valueOf("iconkey" + j), num);
                                        }
                                        list.add(item);
                                        soto_listView.setAdapter(adapter);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        try {
                            URL url = new URL(ServerUrl + "get_out/");
                            task.execute(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 5000, 5000); // 実行したい間隔(ミリ秒)
    }

    public SOTOFragment() {
    }
}