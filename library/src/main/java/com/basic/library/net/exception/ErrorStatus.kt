package com.basic.library.net.exception

/**
 * @author cuixipeng
 * @date 2019-08-21.
 * @description
 */
object ErrorStatus {
    /**
     * 相应成功
     */
    @JvmStatic
    val SUCCESS = 0
    /**
     * 未知错误
     */
    @JvmStatic
    val UNKNOWN_ERROR = 1002
    /**
     * 服务器内部错误
     */
    @JvmStatic
    val SERVER_ERROR = 1003
    /**
     * 网络连接超时
     */
    @JvmStatic
    val NETWORK_ERROR = 1004
    /**
     * API解析异常（或者第三方数据结构更改）等其他异常
     */
    @JvmStatic
    val API_ERROR = 1005
}