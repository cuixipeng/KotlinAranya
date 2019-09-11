package com.aranya.kotlinaranya.ui.fragments.home

import com.aranya.kotlinaranya.bean.HomeBean
import com.basic.library.base.BasePresenter
import com.basic.library.net.exception.Exceptionhandle
import io.reactivex.internal.util.ExceptionHelper

/**
 * @author cuixipeng
 * @date 2019-08-27.
 * @description
 */
class HomePresenter : BasePresenter<HomeContract.View>(), HomeContract.Presenter {
    private var mHomeBean: HomeBean? = null
    private var nextPageUrl: String? = null
    private val homeModel: HomeModel by lazy {
        HomeModel()
    }

    override fun requestHomeData(num: Int) {
        checkViewAttached()//检查是否绑定view
        mRootView?.showLoading()
        val disposable = homeModel.requestHomeData(num).flatMap { homeBean ->
            val bannerItemList = homeBean.issueList[0].itemList
            bannerItemList.filter { item ->
                item.type == "banner2" || item.type == "horizontalScrollCard"
            }.forEach { item ->
                bannerItemList.remove(item)
            }
            mHomeBean = homeBean
            homeModel.loadMoreData(homeBean.nextPageUrl)
        }.subscribe({ homeBean ->
            mRootView?.apply {
                dismissLoading()
                nextPageUrl = homeBean.nextPageUrl
                //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                val newBannerItemList = homeBean.issueList[0].itemList

                newBannerItemList.filter { item ->
                    item.type == "banner2" || item.type == "horizontalScrollCard"
                }.forEach { item ->
                    //移除 item
                    newBannerItemList.remove(item)
                }
                // 重新赋值 Banner 长度
                mHomeBean!!.issueList[0].count = mHomeBean!!.issueList[0].itemList.size
                //赋值过滤后的数据 + banner 数据
                mHomeBean?.issueList!![0].itemList.addAll(newBannerItemList)

                setHomeData(mHomeBean!!)
            }

        }, { t ->
            mRootView?.apply {
                dismissLoading()
                showError(Exceptionhandle.handleException(t), Exceptionhandle.errorCode)
            }
        })
        addSubscription(disposable)

    }

    override fun loadMoreData() {
        val disposabl = nextPageUrl?.let {
            homeModel.loadMoreData(it)
                .subscribe({ homeBean ->
                    mRootView?.apply {
                        val newItemList = homeBean.issueList[0].itemList
                        newItemList.filter { item -> item.type == "banner2" || item.type == "horizontalScrollCard" }
                            .forEach { item ->
                                newItemList.remove(item)
                            }
                        nextPageUrl = homeBean.nextPageUrl
                        setMoreData(newItemList)
                    }
                }, { t ->
                    mRootView?.apply {
                        showError(Exceptionhandle.handleException(t), Exceptionhandle.errorCode)
                    }
                })
        }
        if (disposabl != null) {
            addSubscription(disposabl)
        }
    }

}