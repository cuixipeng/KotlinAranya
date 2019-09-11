package com.aranya.kotlinaranya.ui.fragments.home

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.aranya.kotlinaranya.R
import com.aranya.kotlinaranya.bean.HomeBean
import com.aranya.kotlinaranya.ui.fragments.home.adapter.HomeAdapter
import com.basic.library.base.BaseFragment
import com.basic.library.utils.StatusBarUtil
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author cuixipeng
 * @date 2019-08-22.
 * @description
 */
class HomeFragment : BaseFragment(), HomeContract.View {

    private val mPresenter: HomePresenter by lazy { HomePresenter() }

    private var mTitle: String? = null
    private var num: Int = 1
    private var loadingMore = false
    private var isRefresh = false
    private var mMaterialHeader: MaterialHeader? = null
    private var homeAdapter: HomeAdapter? = null
    private val simpleDateFormat by lazy {
        SimpleDateFormat("- MMM. dd, 'Brunch' -", Locale.ENGLISH)
    }

    companion object {
        fun getInstance(title: String): HomeFragment {
            val fragment = HomeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }


    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initView() {
        mPresenter.attachView(this)
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            isRefresh = true
            mPresenter.requestHomeData(num)
        }
        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        //打开下拉刷新区域块背景：
        mMaterialHeader?.setShowBezierWave(true)
        //设置下拉舒心主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.color_light_black, R.color.color_title_bg)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val childCount = mRecyclerView.childCount
                    val itemCount = mRecyclerView.layoutManager.itemCount
                    val firstVisibleItem =
                        (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (firstVisibleItem + childCount == itemCount) {
                        if (!loadingMore) {
                            loadingMore = true
                            mPresenter.loadMoreData()
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                if (currentVisibleItemPosition == 0) {
                    //背景设置为透明
                    toolbar.setBackgroundColor(getColor(R.color.color_translucent))
                    iv_search.setImageResource(R.mipmap.ic_action_search_white)
                    tv_header_title.text = ""
                } else {
                    if (homeAdapter?.mData!!.size > 1) {
                        toolbar.setBackgroundColor(getColor(R.color.color_title_bg))
                        iv_search.setImageResource(R.mipmap.ic_action_search_black)
                        val itemList = homeAdapter!!.mData
                        val item = itemList[currentVisibleItemPosition + homeAdapter!!.bannerItemSize - 1]
                        if (item.type == "textHeader") {
                            tv_header_title.text = item.data?.text
                        } else {
                            tv_header_title.text = simpleDateFormat.format(item.data?.date)
                        }
                    }
                }
            }
        })

        mLayoutStatusView = multipleStatusView
        //状态栏透明和间距处理
        activity?.let { StatusBarUtil.darkMode(it) }
        activity?.let { StatusBarUtil.setPaddingSmart(it, toolbar) }

    }

    override fun lazyLoad() {
        mPresenter.requestHomeData(num)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun setHomeData(homeBean: HomeBean) {
        mLayoutStatusView?.showContent()
        Logger.d(homeBean)
        //adapter
        homeAdapter = activity?.let { HomeAdapter(it, homeBean.issueList[0].itemList) }
        //设置banner大小
        homeAdapter?.setBannerSize(homeBean.issueList[0].count)
        mRecyclerView.adapter = homeAdapter
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun setMoreData(itemList: ArrayList<HomeBean.Issue.Item>) {
        loadingMore=false
        homeAdapter?.addItemData(itemList)
    }

    override fun showError(msg: String, errorCode: Int) {
    }

    override fun showLoading() {

    }

    override fun dismissLoading() {
    }

    fun getColor(colorId: Int): Int {
        return resources.getColor(colorId)
    }
}
