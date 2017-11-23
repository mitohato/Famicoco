package com.example.mito.famicoco;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mito.famicoco.MainActivity.ServerUrl;
import static com.example.mito.famicoco.MainActivity.myBeaconMacAddress;
import static com.example.mito.famicoco.MainActivity.myName;
import static com.example.mito.famicoco.MainActivity.myRegistarationId;

public class InitStartActivity extends AppCompatActivity {
    @BindView(R.id.name_init_setting)
    EditText editText;
    @BindView(R.id.button_setting)
    Button btn;
    @BindView(R.id.getBeaconID)
    TextView getBeaconID;
    @BindView(R.id.getRegistrationID)
    TextView getRegistrationID;

    public static String name = "";
    private Boolean[] isGetID = {false, false};

    @Override
    protected void onResume() {
        super.onResume();
        final Handler mHandler = new Handler();
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 実行したい処理
                        if (!myBeaconMacAddress.equals("")) {
                            getBeaconID.setText("OK");
                            getBeaconID.setTextColor(getResources().getColor(R.color.Red));
                            isGetID[0] = true;
                        }
                        if (!myRegistarationId.equals("")) {
                            getRegistrationID.setText("OK");
                            getRegistrationID.setTextColor(getResources().getColor(R.color.Red));
                            isGetID[1] = true;
                        }
                        if (isGetID[0] == isGetID[1] && isGetID[0]) {
                            btn.setEnabled(true);
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        ButterKnife.bind(this);
        setTitle("ふぁみここ");
        name = "";


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = editText.getText().toString();
                Log.d("hoge", "foo");
                if (!name.equals("")) {
                    myName = name;

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sp.edit().putString("name", name).commit();

                    try {
                        name = URLEncoder.encode(name, "UTF-8");
                        String urlString = ServerUrl + "init_famicoco/" + "?user_name=" + name + "&regi_id=" + myRegistarationId + "&beacon_id=" + myBeaconMacAddress;
                        URL url = new URL(urlString);
                        new HttpGetTask().execute(url);
                    } catch (MalformedURLException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else if (name.equals("")) {
                    Toast.makeText(InitStartActivity.this, "名前を入力してください", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn.setEnabled(false);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    // ダイアログ表示など特定の処理を行いたい場合はここに記述
                    // 親クラスのdispatchKeyEvent()を呼び出さずにtrueを返す
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}