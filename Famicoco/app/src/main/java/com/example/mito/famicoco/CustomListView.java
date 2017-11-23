package com.example.mito.famicoco;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by mito on 2016/10/24.
 */

/**
 * @author talkKey
 */

public class CustomListView extends ListView {
    //ActivityまたはFragmentが実装するインターフェース
    public interface OnKeyboardAppearedListener {
        public void onKeyboardAppeared(boolean isChange);
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //キーボード出現時（Viewのサイズが小さくなった場合）のみ
        if (h < oldh) {
            //インターフェースを実装したリスナー（Activity、Fragment）のメソッドを呼ぶ
            listener.onKeyboardAppeared(true);
        }
    }
}
