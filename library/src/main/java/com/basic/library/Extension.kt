package com.basic.library

import android.content.Context
import android.widget.Toast
import com.basic.library.base.MyApplication

/**
 * @author cuixipeng
 * @date 2019-08-23.
 * @description
 */
fun Context.showToast(content: String): Toast {
    val toast = Toast.makeText(MyApplication.context, content, Toast.LENGTH_SHORT)
    toast.show()
    return toast
}

/**
 * 格式化时间
 */
fun durationFormat(duration: Long?): String {
    val minute = duration!! / 60
    val second = duration % 60
    return if (minute <= 9) {
        if (second <= 9) {
            "0$minute' 0$second''"
        } else {
            "0$minute' $second''"
        }
    } else {
        if (second <= 9) {
            "$minute' 0$second''"
        } else {
            "$minute' $second''"
        }
    }

}