package com.example.mito.famicoco

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList

import com.example.mito.famicoco.MainActivity.myName

internal class TalkCustomAdapter(c: Context, id: Int, list: ArrayList<CustomData>) : ArrayAdapter<CustomData>(c, id, list) {
    private val layoutInflater: LayoutInflater


    override fun getViewTypeCount(): Int {
        return 2
    }


    init {
        this.layoutInflater = c.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val customData = getItem(position)
        if (customData!!.name != myName) {
            view = layoutInflater.inflate(R.layout.ie_row2, parent, false)
        } else {
            view = layoutInflater.inflate(R.layout.ie_row, parent, false)
        }
        (view.findViewById<View>(R.id.ie2_icon) as ImageView)
                .setImageBitmap(customData.icon)
        (view.findViewById<View>(R.id.ie2_textView) as TextView).text = customData.name
        (view.findViewById<View>(R.id.ie2_textView2) as TextView).text = customData.comment
        (view.findViewById<View>(R.id.time_now) as TextView).text = customData.time_now
        return view
    }

    //タッチ無効
    override fun isEnabled(position: Int): Boolean {
        return false
    }
}
