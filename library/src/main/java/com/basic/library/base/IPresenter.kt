package com.basic.library.base

/**
 * @author cuixipeng
 * @date 2019-08-21.
 * @description
 */
interface IPresenter<in V : IBaseView> {
    fun attachView(mRootView: V)
    
    fun detachView()
}