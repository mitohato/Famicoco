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

import static com.example.mito.famicoco.MainActivity.myName;

class TalkCustomAdapter extends ArrayAdapter<CustomData> {
    private LayoutInflater layoutInflater;


    @Override
    public int getViewTypeCount() {
        return 2;
    }


    TalkCustomAdapter(Context c, int id, ArrayList<CustomData> list) {
        super(c, id, list);
        this.layoutInflater = (LayoutInflater) c.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        CustomData customData = getItem(position);
        if (!customData.getName().equals(myName)) {
            view = layoutInflater.inflate(R.layout.ie_row2, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.ie_row, parent, false);
        }
        ((ImageView) view.findViewById(R.id.ie2_icon))
                .setImageBitmap(customData.getIcon());
        ((TextView) view.findViewById(R.id.ie2_textView))
                .setText(customData.getName());
        ((TextView) view.findViewById(R.id.ie2_textView2))
                .setText(customData.getComment());
        ((TextView) view.findViewById(R.id.time_now))
                .setText(customData.getTime_now());
        return view;
    }

    //タッチ無効
    public boolean isEnabled(int position) {
        return false;
    }
}
