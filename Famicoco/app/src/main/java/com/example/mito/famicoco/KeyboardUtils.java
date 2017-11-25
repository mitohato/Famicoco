package com.example.mito.famicoco;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

class KeyboardUtils {        //キーボードの出し入れをするクラス

    static void hide(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    static void hide(Activity activity) {
        hide(activity, activity.getCurrentFocus());
    }

    public static void initHidden(Activity activity) {
        activity.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void show(Context context, EditText edit) {
        show(context, edit, 0);
    }

    private static void show(final Context context, final EditText edit, int delayTime) {
        final Runnable showKeyboardDelay = new Runnable() {
            @Override
            public void run() {
                if (context != null) {
                    InputMethodManager imm = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        };
        new Handler().postDelayed(showKeyboardDelay, delayTime);
    }
}