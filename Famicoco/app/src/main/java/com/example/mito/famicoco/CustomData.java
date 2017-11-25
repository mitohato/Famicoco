package com.example.mito.famicoco;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class CustomData {       //カスタムデータをセットするためのクラス
    private Bitmap icon;
    private String name;
    private String comment;
    private String time_now;

    void setTime(String time) {
        this.time_now = time;
    }

    void setTime() {
        @SuppressLint("SimpleDateFormat") final DateFormat df = new SimpleDateFormat("HH:mm");
        final Date date = new Date(System.currentTimeMillis());
        this.time_now = df.format(date);
    }

    String getTime_now() {
        return time_now;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setComment(String comment) {
        this.comment = comment;
    }

    String getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

    public Bitmap getIcon() {
        return icon;
    }


}

