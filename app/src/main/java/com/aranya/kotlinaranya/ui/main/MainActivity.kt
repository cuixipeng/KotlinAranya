package com.aranya.kotlinaranya.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import com.aranya.kotlinaranya.R
import com.aranya.kotlinaranya.bean.TabEntity
import com.aranya.kotlinaranya.ui.fragments.AttentionFragment
import com.aranya.kotlinaranya.ui.fragments.home.HomeFragment
import com.aranya.kotlinaranya.ui.fragments.MessageFragment
import com.aranya.kotlinaranya.ui.fragments.MineFragment
import com.basic.library.base.BaseActivity
import com.basic.library.showToast
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author cuixipeng
 * @description  程序主页
 * @date 2019年08月20日
 */
class MainActivity : BaseActivity() {
    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    private val mTitles = arrayOf("首页", "关注", "", "消息", "我的")
    // 未被选中的图标
    private val mIconUnSelectIds = intArrayOf(0, 0, R.mipmap.ic_mine_normal, 0, 0)
    // 被选中的图标
    private val mIconSelectIds = intArrayOf(0, 0, R.mipmap.ic_mine_selected, 0, 0)
    private val mTabEntities = ArrayList<CustomTabEntity>()
    private var mIndex: Int = 0//默认为0
    private var mHomeFragment: HomeFragment? = null
    private var mAttentionFragment: AttentionFragment? = null
    private var mMessageFragment: MessageFragment? = null
    private var mMineFragment: MineFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt("currTabIndex")
        }
        super.onCreate(savedInstanceState)
        initTabs()
        tab_layout.currentTab = mIndex
        switchFragment(mIndex)
    }

    /**
     * 初始化底部菜单栏
     */
    private fun initTabs() {
        (0 until mTitles.size).mapTo(mTabEntities) {
            TabEntity(mTitles[it], mIconSelectIds[it], mIconUnSelectIds[it])
        }
        //为tab赋值
        tab_layout.setTabData(mTabEntities)
        tab_layout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                switchFragment(position)
            }

            override fun onTabReselect(position: Int) {
            }
        })
    }

    private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragment(transaction)
        when (position) {
            0 -> mHomeFragment?.let { transaction.show(it) } ?: HomeFragment.getInstance(mTitles[position]).let {
                mHomeFragment = it
                transaction.add(R.id.fl_container, it, "home")
            }
            0 -> mAttentionFragment?.let { transaction.show(it) }
                ?: AttentionFragment.getInstance(mTitles[position]).let {
                    mAttentionFragment = it
                    transaction.add(R.id.fl_container, it, "attention")
                }
            1 -> mMessageFragment?.let { transaction.show(it) }
                ?: MessageFragment.getInstance(mTitles[position]).let {
                    mMessageFragment = it
                    transaction.add(R.id.fl_container, it, "message")
                }
            2 -> mMineFragment?.let { transaction.show(it) }
                ?: MineFragment.getInstance(mTitles[position]).let {
                    mMineFragment = it
                    transaction.add(R.id.fl_container, it, "mine")
                }
            else -> {

            }
        }
        mIndex = position
        tab_layout.currentTab = mIndex
        transaction.commitAllowingStateLoss()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        mHomeFragment?.let { transaction.hide(it) }
        mAttentionFragment?.let { transaction.hide(it) }
        mMessageFragment?.let { transaction.hide(it) }
    }


    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        showToast("onSaveInstanceState->" + mIndex)
        super.onSaveInstanceState(outState)
        //记录fragment的位置,防止崩溃 activity被系统回收时，fragment错乱
        if (tab_layout != null) {
            outState.putInt("currTabIndex", mIndex)
        }
    }

    override fun initView() {
    }

    override fun initData() {
    }

    override fun startRequest() {
    }

    private var mExitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
                finish()
            } else {
                mExitTime = System.currentTimeMillis()
                showToast("再按一次退出程序")
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
