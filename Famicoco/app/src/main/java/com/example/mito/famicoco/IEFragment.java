package com.example.mito.famicoco;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mito.famicoco.MainActivity.ServerUrl;
import static com.example.mito.famicoco.MainActivity.myName;
import static com.example.mito.famicoco.MainActivity.selectIcon_map;

public class IEFragment extends Fragment implements CustomListView.OnKeyboardAppearedListener {      //いえここ用のフラグメント
    private TalkCustomAdapter adapter;
    private Timer mTimer = null;
    private Handler mHandler = null;
    private CustomData item;
    public static Bitmap[] icon = new Bitmap[6];
    private ArrayList<CustomData> list;
    private ArrayList<Object> ie_list;


    @BindView(R.id.ie_listView)
    CustomListView ie_listView;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button)
    Button button;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ie, container, false);
        ButterKnife.bind(this, v);
        ie_listView.setListener(this);
        return v;
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
                        @SuppressLint("StaticFieldLeak") AsyncTask<URL, Void, ArrayList<UpdateItem>> task = new AsyncTask<URL, Void, ArrayList<UpdateItem>>() {
                            @Override
                            protected ArrayList<UpdateItem> doInBackground(URL... urls) {

                                HttpURLConnection con;
                                String readSt;

                                UpdateItem updateItem;
                                ArrayList<UpdateItem> updateItemList = new ArrayList<>();
                                try {
                                    final URL url = urls[0];
                                    con = (HttpURLConnection) url.openConnection();
                                    con.setDoInput(true);
                                    con.connect();
                                    InputStream in = con.getInputStream();
                                    readSt = HttpGetTask.readInputStream(in);
                                    JSONArray data = new JSONArray(readSt);
                                    JSONArray member = data.getJSONArray(0);
                                    ie_list.clear();
                                    int m = member.length();
                                    for (int i = 0; i < m; i++) {
                                        ie_list.add(member.get(i));
                                    }
                                    JSONArray talks = data.getJSONArray(1);
                                    m = talks.length();
                                    for (int i = 0; i < m; i++) {
                                        JSONObject talk = talks.getJSONObject(i);
                                        updateItem = new UpdateItem(talk);
                                        updateItemList.add(updateItem);
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                                return updateItemList;
                            }

                            @Override
                            protected void onPostExecute(ArrayList<UpdateItem> updateItemArrayList) {
                                int m = ie_list.size();
                                imageView1.setImageResource(R.color.SubColor);
                                imageView2.setImageResource(R.color.SubColor);
                                imageView3.setImageResource(R.color.SubColor);
                                imageView4.setImageResource(R.color.SubColor);
                                imageView5.setImageResource(R.color.SubColor);
                                imageView6.setImageResource(R.color.SubColor);
                                for (int i = 0; i < m; i++) {
                                    if (i == 0) imageView1.setImageBitmap(judge(ie_list.get(0)));
                                    else if (i == 1)
                                        imageView2.setImageBitmap(judge(ie_list.get(1)));
                                    else if (i == 2)
                                        imageView3.setImageBitmap(judge(ie_list.get(2)));
                                    else if (i == 3)
                                        imageView4.setImageBitmap(judge(ie_list.get(3)));
                                    else if (i == 4)
                                        imageView5.setImageBitmap(judge(ie_list.get(4)));
                                    else if (i == 5)
                                        imageView6.setImageBitmap(judge(ie_list.get(5)));
                                }
                                if (list.size() != updateItemArrayList.size()) {
                                    list.clear();
                                    adapter = new TalkCustomAdapter(getContext(), 0, list);
                                    m = updateItemArrayList.size();
                                    for (int i = 0; i < m; i++) {
                                        UpdateItem updateItem = updateItemArrayList.get(i);
                                        list.add(0, updateItem.toCustomData());
                                        ie_listView.setAdapter(adapter);
                                    }
                                    onKeyboardAppeared();
                                }
                            }
                        };
                        try {
                            URL url = new URL(ServerUrl + "home/");
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        icon[0] = BitmapFactory.decodeResource(getResources(), R.drawable.haruka);
        icon[1] = BitmapFactory.decodeResource(getResources(), R.drawable.riku);
        icon[2] = BitmapFactory.decodeResource(getResources(), R.drawable.mother);
        icon[3] = BitmapFactory.decodeResource(getResources(), R.drawable.father);
        icon[4] = BitmapFactory.decodeResource(getResources(), R.drawable.grandfather);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);

        list = new ArrayList<>();

        //いえここの送るボタンに機能を設定
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString();
                item = new CustomData();
                adapter = new TalkCustomAdapter(getContext(), 0, list);

                if (!str.equals("")) {
                    String text = str;
                    try {
                        str = URLEncoder.encode(str, "UTF-8");
                        String name = URLEncoder.encode(myName, "UTF-8");
                        String urlString = ServerUrl + "home/talk/?name=" + name + "&text=" + str;
                        URL url = new URL(urlString);
                        new HttpGetTask().execute(url);
                    } catch (MalformedURLException e) {
                        Toast.makeText(getContext(), "connection failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        Toast.makeText(getContext(), "Encoding failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    item.setComment(text);
                    item.setName(myName);
                    item.setTime();
                    list.add(item);
                    editText.setText("");
                    ie_listView.setAdapter(adapter);
                    onKeyboardAppeared();
                }
                KeyboardUtils.hide(getActivity());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public IEFragment() {
        ie_list = new ArrayList<>();
    }

    @Override
    public void onKeyboardAppeared() {

        //ListView生成済、且つサイズ変更した（キーボードが出現した）場合
//        if (isChange) {
            //スクロールアニメーションが要らない場合はこれでOK
            ie_listView.setSelection(ie_listView.getCount() - 1);
//        }
    }

    protected static Bitmap judge(Object s) {
        Bitmap bitmap;
        if (s.equals("しゅり")) bitmap = icon[0];
        else if (s.equals("しゅうき")) bitmap = icon[1];
        else if (s.equals("おかあさん")) bitmap = icon[2];
        else if (s.equals("おとうさん")) bitmap = icon[3];
        else if (s.equals("かんじい")) bitmap = icon[4];
        else bitmap = selectIcon_map.get(s);
        return bitmap;
    }
}

class UpdateItem {
    private String name;
    private String comment;
    private String time;
    private Bitmap icon;

    UpdateItem(JSONObject json) throws JSONException {
        this.name = json.getString("name");
        this.comment = json.getString("text");
        this.time = json.getString("time");
        switch (name) {
            case "しゅり":
                this.icon = IEFragment.icon[0];
                break;
            case "しゅうき":
                this.icon = IEFragment.icon[1];
                break;
            case "お母さん":
                this.icon = IEFragment.icon[2];
                break;
            case "お父さん":
                this.icon = IEFragment.icon[3];
                break;
            case "かん爺":
                this.icon = IEFragment.icon[4];
                break;
            default:
                this.icon = IEFragment.icon[5];
                break;
        }
    }

    CustomData toCustomData() {
        CustomData customData = new CustomData();
        customData.setTime(this.time);
        customData.setComment(this.comment);
        customData.setName(this.name);
        // ここにif文を書く
        customData.setIcon(this.icon);
        return customData;
    }
}