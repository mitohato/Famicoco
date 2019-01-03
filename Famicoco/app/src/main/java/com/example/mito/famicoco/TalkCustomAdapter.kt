package com.example.mito.famicoco

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mito.famicoco.MainActivity.Companion.myName
import java.util.ArrayList

internal class TalkCustomAdapter(
        c: Context?,
        id: Int,
        list: ArrayList<CustomData>?
) : ArrayAdapter<CustomData>(
        c,
        id,
        list
) {
    private val layoutInflater: LayoutInflater = c?.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
    ) as LayoutInflater
    
    override fun getViewTypeCount(): Int {
        return 2
    }
    
    override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
    ): View {
        val view: View
        val customData = getItem(position)
        view = if (customData?.name != myName) {
            layoutInflater.inflate(
                    R.layout.ie_row2,
                    parent,
                    false
            )
        } else {
            layoutInflater.inflate(
                    R.layout.ie_row,
                    parent,
                    false
            )
        }
        customData?.let {
            (view.findViewById<View>(R.id.ie2_icon) as ImageView)
                    .setImageBitmap(it.icon)
            (view.findViewById<View>(R.id.ie2_textView) as TextView).text = it.name
            (view.findViewById<View>(R.id.ie2_textView2) as TextView).text = it.comment
            (view.findViewById<View>(R.id.time_now) as TextView).text = it.timeNow
        }
        return view
    }
    
    // タッチ無効
    override fun isEnabled(position: Int): Boolean {
        return false
    }
}
