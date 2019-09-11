package com.aranya.kotlinaranya.bean

import com.flyco.tablayout.listener.CustomTabEntity

/**
 * @author cuixipeng
 * @date 2019-08-22.
 * @description 首页底部bean
 */
class TabEntity(var title: String, private var selectedIcon: Int, private var unSelectedIcon: Int) : CustomTabEntity {
    override fun getTabUnselectedIcon(): Int {
        return unSelectedIcon
    }

    override fun getTabSelectedIcon(): Int {
        return selectedIcon
    }

    override fun getTabTitle(): String {
        return title
    }

}