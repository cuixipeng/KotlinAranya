package com.basic.library.net.exception

import java.lang.RuntimeException

/**
 * @author cuixipeng
 * @date 2019-08-21.
 * @description
 */
class ApiException : RuntimeException {
    private var code: Int? = null

    constructor(throwable: Throwable, code: Int) : super(throwable) {
        this.code = code
    }

    constructor(message: String) : super(Throwable(message))
}