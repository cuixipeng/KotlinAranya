package com.aranya.kotlinaranya.ui.fragments.home

import com.aranya.kotlinaranya.api.MainApi
import com.aranya.kotlinaranya.bean.HomeBean
import com.basic.library.net.RetrofitManager
import com.basic.library.rx.scheduler.SchedulerUtils
import io.reactivex.Observable

/**
 * @author cuixipeng
 * @date 2019-08-28.
 * @description
 */
class HomeModel {

    val mApi: MainApi by lazy {
        RetrofitManager.apiService(MainApi::class.java)
    }

    /**
     * 获取首页 Banner 数据
     */
    fun requestHomeData(num: Int): Observable<HomeBean> {
        return mApi.getFirstHomeData(num)
            .compose(SchedulerUtils.ioToMain())
    }

    /**
     * 加载更多
     */
    fun loadMoreData(url: String): Observable<HomeBean> {
        return mApi.getMoreHomeData(url)
            .compose(SchedulerUtils.ioToMain())
    }
}


