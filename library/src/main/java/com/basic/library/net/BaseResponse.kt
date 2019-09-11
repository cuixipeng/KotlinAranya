package com.basic.library.net

/**
 * @author cuixipeng
 * @date 2019-08-21.
 * @description 服务器返回数据格式
 */
class BaseResponse<T>(val code: Int, val msg: String, val data: T)