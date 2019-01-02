package com.example.mito.famicoco;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class CustomAdapter extends ArrayAdapter<CustomData> {  //リストに表示するためのadapterを用意するクラス
    private LayoutInflater layoutInflater;

    CustomAdapter(Context c, int id, ArrayList<CustomData> list) {
        super(c, id, list);
        this.layoutInflater = (LayoutInflater) c.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.tl_row, parent, false);
        }

        CustomData customData = getItem(position);
        assert customData != null;
        ((ImageView) convertView.findViewById(R.id.icon))
                .setImageBitmap(customData.getIcon());
        ((TextView) convertView.findViewById(R.id.time_now))
                .setText(customData.getTime_now());
        ((TextView) convertView.findViewById(R.id.tl_text))
                .setText(customData.getComment());
        return convertView;
    }

    //タッチ無効
    public boolean isEnabled(int position) {
        return false;
    }
}