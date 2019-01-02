package com.example.mito.famicoco

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

/**
 * @author talkKey
 */

class CustomListView : ListView {

    private var listener: OnKeyboardAppearedListener? = null

    //ActivityまたはFragmentが実装するインターフェース
    interface OnKeyboardAppearedListener {
        fun onKeyboardAppeared()
    }


    constructor(context: Context) : super(context) {}

    //このコンストラクタが無いとXMLからインフレートできないので注意
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet,
                defStyle: Int) : super(context, attrs, defStyle) {
    }

    //リスナー登録のメソッド
    fun setListener(listener: OnKeyboardAppearedListener) {
        this.listener = listener
    }

    //Viewのサイズが変化した時に呼ばれるメソッド
    override fun onSizeChanged(w: Int, h: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(w, h, oldWidth, oldHeight)

        //キーボード出現時（Viewのサイズが小さくなった場合）のみ
        if (h < oldHeight) {
            //インターフェースを実装したリスナー（Activity、Fragment）のメソッドを呼ぶ
            listener!!.onKeyboardAppeared()
        }
    }
}
