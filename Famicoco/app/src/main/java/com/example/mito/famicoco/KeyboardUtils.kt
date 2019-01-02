package com.example.mito.famicoco

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.WindowManager.LayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

internal object KeyboardUtils {        //キーボードの出し入れをするクラス

    fun hide(context: Context, view: View?) {
        if (view != null) {
            val imm = context.getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    fun hide(activity: Activity) {
        hide(activity, activity.currentFocus)
    }

    fun initHidden(activity: Activity) {
        activity.window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    fun show(context: Context, edit: EditText) {
        show(context, edit, 0)
    }

    private fun show(context: Context?, edit: EditText, delayTime: Int) {
        val showKeyboardDelay = Runnable {
            if (context != null) {
                val imm = context
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        Handler().postDelayed(showKeyboardDelay, delayTime.toLong())
    }
}