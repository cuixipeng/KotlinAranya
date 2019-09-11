package com.basic.library.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.aranya.multiple_status_view.MultipleStatusView
import io.reactivex.annotations.NonNull
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author cuixipeng
 * @description  base 基类
 * @date 2019年08月20日
 */
abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    /**
     * 多种状态view 切换
     */
    protected var mLayoutStatusView: MultipleStatusView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        initData()
        initView()
        startRequest()
        initListener()
    }

    /**
     *  加载布局
     */
    abstract fun layoutId(): Int

    abstract fun initView()

    abstract fun initData()

    private fun initListener() {
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        startRequest()
    }

    /**
     * 开始请求
     */
    abstract fun startRequest()

    /**
     * 打开软键盘
     */
    fun openKeyBord(mEditText: EditText, mContext: Context) {
        val inm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
        inm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * 关闭软键盘
     */
    fun colseKeyBord(mEditText: EditText, mContext: Context) {
        val inm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inm.hideSoftInputFromWindow(mEditText.windowToken, 0)
    }

    /**
     * 重写要申请权限的Activity或者Fragment的onRequestPermissionsResult()方法，
     * 在里面调用EasyPermissions.onRequestPermissionsResult()，实现回调。
     *
     * @param requestCode  权限请求的识别码
     * @param permissions  申请的权限
     * @param grantResults 授权结果
     */
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * 当权限被成功申请的时候执行回调
     *
     * @param requestCode 权限请求的识别码
     * @param perms       申请的权限的名字
     */
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i("EasyPermissions", "获取成功的权限$perms")
    }

    /**
     * 当权限申请失败的时候执行的回调
     *
     * @param requestCode 权限请求的识别码
     * @param perms       申请的权限的名字
     */
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        //处理权限名字字符串
        val sb = StringBuffer()
        for (str in perms) {
            sb.append(str)
            sb.append("\n")
        }
        sb.replace(sb.length - 2, sb.length, "")
        //用户点击拒绝并不在询问时候调用
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Toast.makeText(this, "已拒绝权限" + sb + "并不再询问", Toast.LENGTH_SHORT).show()
            AppSettingsDialog.Builder(this)
                .setRationale("此功能需要" + sb + "权限，否则无法正常使用，是否打开设置")
                .setPositiveButton("好")
                .setNegativeButton("不行")
                .build()
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.getRefWatcher(this)?.watch(this)
    }
}