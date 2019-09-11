package com.basic.library.view.recyclerview

import java.text.FieldPosition

/**
 * @author cuixipeng
 * @date 2019-08-29.
 * @description
 */
interface MultipleType<in T> {
    fun getLayoutId(item: T, position: Int): Int
}