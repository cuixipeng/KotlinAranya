package com.basic.library.rx.scheduler

/**
 * @author cuixipeng
 * @date 2019-08-28.
 * @description
 */
object SchedulerUtils {
    fun <T> ioToMain(): IoMainScheduler<T> {
        return IoMainScheduler()
    }
}