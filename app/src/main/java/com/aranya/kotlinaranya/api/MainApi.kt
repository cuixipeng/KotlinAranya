package com.aranya.kotlinaranya.api

import com.aranya.kotlinaranya.bean.HomeBean
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @author cuixipeng
 * @date 2019-08-20.
 * @description
 */
interface MainApi {
    /**
     * 首页精选
     */
    @GET("v2/feed?")
    fun getFirstHomeData(@Query("num") num: Int): Observable<HomeBean>
    /**
     * 根据 nextPageUrl 请求数据下一页数据
     */
    @GET
    fun getMoreHomeData(@Url url: String): Observable<HomeBean>
}