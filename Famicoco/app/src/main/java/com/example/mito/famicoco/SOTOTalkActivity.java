package com.example.mito.famicoco;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.mito.famicoco.MainActivity.ServerUrl;
import static com.example.mito.famicoco.MainActivity.myName;

public class SOTOTalkActivity extends AppCompatActivity implements CustomListView.OnKeyboardAppearedListener { //そとここのリストを押したときに呼ばれるトーク画面用Activity

    @BindView(R.id.soto_talk_listview)
    CustomListView listView;
    @BindView(R.id.button)
    Button btn;
    @BindView(R.id.soto_editText)
    EditText editText;
    @BindView(R.id.ie_imageView)
    ImageView imageView1;
    @BindView(R.id.ie_imageView2)
    ImageView imageView2;
    @BindView(R.id.ie_imageView3)
    ImageView imageView3;
    @BindView(R.id.ie_imageView4)
    ImageView imageView4;
    @BindView(R.id.ie_imageView5)
    ImageView imageView5;
    @BindView(R.id.ie_imageView6)
    ImageView imageView6;

    private Timer mTimer = null;
    private Handler mHandler = null;
    private ArrayList<CustomData> list;
    private TalkCustomAdapter adapter;
    private int pos = 0;
    private String data, str;
    private JSONArray array, select, tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sototalk);
        ButterKnife.bind(this);
        setTitle("そとここ");

        Intent i = getIntent();
        data = i.getStringExtra("list");
        pos = i.getIntExtra("select", 0) + 1;

        list = new ArrayList<>();
        listView.setListener(this);

        imageView1.setImageResource(R.color.SubColor);
        imageView2.setImageResource(R.color.SubColor);
        imageView3.setImageResource(R.color.SubColor);
        imageView4.setImageResource(R.color.SubColor);
        imageView5.setImageResource(R.color.SubColor);
        imageView6.setImageResource(R.color.SubColor);
        try {
            array = new JSONArray(data);
            select = array.getJSONArray(pos);
            int m = select.length();
            for (int j = 0; j < m; j++) {
                select.get(j);
                if (j == 0) imageView1.setImageBitmap(IEFragment.judge(select.get(0)));
                else if (j == 1) imageView2.setImageBitmap(IEFragment.judge(select.get(1)));
                else if (j == 2) imageView3.setImageBitmap(IEFragment.judge(select.get(2)));
                else if (j == 3) imageView4.setImageBitmap(IEFragment.judge(select.get(3)));
                else if (j == 4) imageView5.setImageBitmap(IEFragment.judge(select.get(4)));
                else if (j == 5) imageView5.setImageBitmap(IEFragment.judge(select.get(5)));
                else imageView6.setImageBitmap(IEFragment.judge(select.get(6)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //戻るボタン表示
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        adapter = new TalkCustomAdapter(this, 0, list);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str = editText.getText().toString();
                CustomData item = new CustomData();
                if (!str.equals("")) {
                    AsyncTask<URL, Void, Boolean> task = new AsyncTask<URL, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(URL... urls) {
                            final URL url = urls[0];
                            JSONArray array = new JSONArray();
                            JSONArray myData = new JSONArray();
                            try {
                                array.put(select);
                                myData.put(myName);
                                myData.put(str);
                                array.put(1, myData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            OkHttpClient client = new OkHttpClient();

                            RequestBody body = new FormBody.Builder()
                                    .addEncoded("data", array.toString())
                                    .build();

                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(body)
                                    .build();

                            Log.d("string", request.toString());
                            Log.d("string", select.toString());
                            Log.d("string", body.toString());
                            Log.d("string", array.toString());
                            try {
                                Response res = client.newCall(request).execute();
                                if (!(res == null)) Log.d("res", res.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    };

                    try {
                        URL url = new URL(ServerUrl + "out/poyo/");
                        task.execute(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    item.setComment(str);
                    item.setName(myName);
                    item.setTime();
                    list.add(item);
                    editText.setText("");
                    listView.setAdapter(adapter);
                }
                KeyboardUtils.hide(getApplicationContext(), view);
            }
        });

    }

    @Override
    public void onKeyboardAppeared(boolean isChange) {

        //ListView生成済、且つサイズ変更した（キーボードが出現した）場合
        if (isChange) {

            //リストアイテムの総数-1（0番目から始まって最後のアイテム）にスクロールさせる
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //リストアイテムの総数-1（0番目から始まって最後のアイテム）にフォーカスさせる
                    listView.smoothScrollToPosition(listView.getCount() - 1);
                }
            };
            handler.postDelayed(runnable, 500);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler = new Handler();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 実行したい処理
                        AsyncTask<URL, Void, ArrayList<UpdateItem>> task = new AsyncTask<URL, Void, ArrayList<UpdateItem>>() {
                            @Override
                            protected ArrayList<UpdateItem> doInBackground(URL... urls) {
                                final URL url = urls[0];
                                String result;

                                OkHttpClient client = new OkHttpClient();

                                RequestBody body = new FormBody.Builder()
                                        .add("data", select.toString())
                                        .build();
                                Request request = new Request.Builder()
                                        .url(url)
                                        .get()
                                        .post(body)
                                        .build();

                                UpdateItem updateItem;
                                ArrayList<UpdateItem> updateItemList = new ArrayList<>();
                                try {
                                    Response response = client.newCall(request).execute();
                                    Log.d("response", response.toString());
                                    Log.d("response", response.body().toString());
                                    result = response.body().string();

                                    Log.d("string", result);
                                    tmp = new JSONArray(result);
                                    int m;
                                    m = tmp.length();
                                    for (int i = 0; i < m; i++) {
                                        JSONObject talk = tmp.getJSONObject(i);
                                        updateItem = new UpdateItem(talk);
                                        updateItemList.add(updateItem);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }


                                return updateItemList;
                            }

                            @Override
                            protected void onPostExecute(ArrayList<UpdateItem> updateItemArrayList) {

                                int m;
                                if (list.size() != updateItemArrayList.size()) {
                                    list.clear();
                                    adapter = new TalkCustomAdapter(getApplicationContext(), 0, list);
                                    m = updateItemArrayList.size();
                                    for (int i = 0; i < m; i++) {
                                        UpdateItem updateItem = updateItemArrayList.get(i);
                                        list.add(0, updateItem.toCustomData());
                                        listView.setAdapter(adapter);
                                    }
                                    onKeyboardAppeared(true);
                                }
                            }
                        };
                        try {
                            URL url = new URL(ServerUrl + "out/talk/");
                            task.execute(url);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 5000, 5000); // 実行したい間隔(ミリ秒)
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    // ActionBarの「<」を押した時に元の画面に戻るように
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
