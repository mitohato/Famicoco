package com.example.mito.famicoco

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList

internal class CustomAdapter(
        c: Context?,
        id: Int,
        list: ArrayList<CustomData>?
) : ArrayAdapter<CustomData>(c, id, list) { // リストに表示するためのadapterを用意するクラス
    private val layoutInflater: LayoutInflater = c?.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
    ) as LayoutInflater
    
    override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
    ): View? {
        val view = convertView ?: layoutInflater.inflate(
                R.layout.tl_row,
                parent,
                false
        )
        
        val customData = getItem(position) ?: return null
        customData.also {
            (view.findViewById<View>(R.id.icon) as ImageView).setImageBitmap(it.icon)
            (view.findViewById<View>(R.id.time_now) as TextView).text = it.timeNow
            (view.findViewById<View>(R.id.tl_text) as TextView).text = it.comment
        }
        return view
    }
    
    // タッチ無効
    override fun isEnabled(position: Int): Boolean {
        return false
    }
}