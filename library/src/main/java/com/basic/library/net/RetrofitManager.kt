package com.basic.library.net


import com.basic.library.api.UrlConstant
import com.basic.library.base.MyApplication
import com.basic.library.utils.NetwrokUtil
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author cuixipeng （可以写固定的名称或者使用计算机用户名  ）
 * @description 是用object 定义为单例
 * @date 2019-08-20.
 */
object RetrofitManager {

    fun <T> apiService(service: Class<T>): T {
        return getRetrofit().create(service)
    }


    public fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(UrlConstant.BASE_URL)
            .client(getOkhttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkhttpClient(): OkHttpClient {
        //添加一个log拦截器，打印所有的log
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        //可以设置请求过滤的水平，body basic ，headers
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        //设置 请求的缓存的大小跟位置
        val cacheFile = File(MyApplication.context.cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50)//50M缓存大小
        return OkHttpClient.Builder()
            .addInterceptor(addQueryParameterInterceptor())//参数添加
            .addInterceptor(addHeaderInterceptor())//header
            .addInterceptor(addCacheInterceptor())//缓存
            .addInterceptor(httpLoggingInterceptor)//日志拦截
            .cache(cache)
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
            .build()

    }

    private fun addCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!NetwrokUtil.isNetWorkAvailable(MyApplication.context)) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            }
            val response = chain.proceed(request)
            if (NetwrokUtil.isNetWorkAvailable(MyApplication.context)) {
                val maxAge = 0
                //有网络时，设置缓存超过0小时，意思就是不读取缓存数据，只对get有用，post没有缓存
                response.newBuilder()
                    .header("Cache-Control", "public,max-age=" + maxAge)
                    .removeHeader("Retrofit")//清除头信息，因为服务器如果不支持就会返回一些干扰信息，不清除下面无法生效
                    .build()
            } else {
                // 存
                val maxState = 60 * 60 * 24 * 28
                response.newBuilder()
                    .header("Cache-Control", "public,only-if_cached,max-stale=" + maxState)
                    .removeHeader("nyn")
                    .build()
            }
            response
        }
    }

    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("token", "")
                .method(originalRequest.method(), originalRequest.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val request: Request
            val modifiedUrl = originalRequest.url().newBuilder()
                .addQueryParameter("channel", "")
                .build()
            request = originalRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
    }


}

