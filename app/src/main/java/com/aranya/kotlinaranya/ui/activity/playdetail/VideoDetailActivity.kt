package com.aranya.kotlinaranya.ui.activity.playdetail

import android.annotation.TargetApi
import android.os.Build
import android.support.v4.view.ViewCompat
import android.transition.Transition
import com.aranya.kotlinaranya.R
import com.basic.library.base.BaseActivity
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_video_detail.*

/**
 * @author cuixipeng
 * @date 2019-09-10.
 * @description 播放详情界面
 */
class VideoDetailActivity : BaseActivity() {
    private var isTransition: Boolean = false

    private var transition: Transition? = null

    companion object {
        const val IMG_TRANSITION = "IMG_TRANSITION"
        const val TRANSITION = "TRANSITION"
    }

    override fun layoutId(): Int = R.layout.activity_video_detail

    override fun initView() {
        initTransition()
    }

    override fun initData() {
        isTransition = intent.getBooleanExtra(TRANSITION, false)
    }

    override fun startRequest() {

    }

    private fun initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
            ViewCompat.setTransitionName(mVideoView, IMG_TRANSITION)
            addTransitionListener()
            startPostponedEnterTransition()
        } else {
//            loadVideoInfo()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addTransitionListener() {
        transition = window.sharedElementEnterTransition
        transition?.addListener(object : Transition.TransitionListener {
            override fun onTransitionResume(p0: Transition?) {
            }

            override fun onTransitionPause(p0: Transition?) {
            }

            override fun onTransitionCancel(p0: Transition?) {
            }

            override fun onTransitionStart(p0: Transition?) {
            }

            override fun onTransitionEnd(p0: Transition?) {
                Logger.d("onTransitionEnd()------")
//                loadVideoInfo()
                transition?.removeListener(this)
            }

        })
    }
}