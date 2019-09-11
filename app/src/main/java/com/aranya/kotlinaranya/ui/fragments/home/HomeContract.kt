package com.aranya.kotlinaranya.ui.fragments.home

import com.aranya.kotlinaranya.bean.HomeBean
import com.basic.library.base.BasePresenter
import com.basic.library.base.IBaseView

/**
 * @author cuixipeng
 * @date 2019-08-27.
 * @description 契约类
 */
interface HomeContract {
    interface View : IBaseView {
        /**
         * 设置第一次请求的数据
         */
        fun setHomeData(homeBean: HomeBean)

        /**
         * 设置加载更多的数据
         */
        fun setMoreData(itemList: ArrayList<HomeBean.Issue.Item>)

        /**
         * 显示错误
         */
        fun showError(msg: String, errorCode: Int)
    }

    interface Presenter{
        /**
         * 获取首页数据
         */
        fun requestHomeData(num: Int)

        /**
         * 加载更多
         */
        fun loadMoreData()
    }
}