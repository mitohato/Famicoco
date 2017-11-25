package com.example.mito.famicoco;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author talkKey
 */

public class CustomListView extends ListView {
    //ActivityまたはFragmentが実装するインターフェース
    public interface OnKeyboardAppearedListener {
        void onKeyboardAppeared();
    }

    private OnKeyboardAppearedListener listener;


    public CustomListView(Context context) {
        super(context);
    }

    //このコンストラクタが無いとXMLからインフレートできないので注意
    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
    }

    //リスナー登録のメソッド
    public void setListener(OnKeyboardAppearedListener listener) {
        this.listener = listener;
    }

    //Viewのサイズが変化した時に呼ばれるメソッド
    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);

        //キーボード出現時（Viewのサイズが小さくなった場合）のみ
        if (h < oldHeight) {
            //インターフェースを実装したリスナー（Activity、Fragment）のメソッドを呼ぶ
            listener.onKeyboardAppeared();
        }
    }
}
