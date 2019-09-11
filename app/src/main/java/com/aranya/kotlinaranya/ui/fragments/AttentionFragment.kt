package com.aranya.kotlinaranya.ui.fragments

import android.os.Bundle
import com.aranya.kotlinaranya.R
import com.basic.library.base.BaseFragment

/**
 * @author cuixipeng
 * @date 2019-08-27.
 * @description
 */
class AttentionFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_attention
    }

    override fun initView() {
    }

    override fun lazyLoad() {
    }
    private var mTitle: String? = null

    companion object {
        fun getInstance(title: String): AttentionFragment {
            val fragment = AttentionFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }
}