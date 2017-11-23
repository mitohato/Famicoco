package com.example.mito.famicoco;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class ShowDiaLog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_dia_log);

        Intent i = getIntent();
        String text = i.getStringExtra("text");
        String title = i.getStringExtra("title");
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(text);
        alertBuilder.setPositiveButton("閉じる", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowDiaLog.this.finish();//選択をしたら自信のActivityを終了させる
            }
        });
        alertBuilder.create().show();
    }
}
