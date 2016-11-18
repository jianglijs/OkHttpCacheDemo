package com.protruly.okhttpdemo;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Jiangli
 * @version 1.0
 * @date 2016/11/11 16:04
 */
public class HttpClient {
    private static final String ZHIHU_ZHUANLAN_API = "https://zhuanlan.zhihu.com/api/recommendations/columns?limit=12&offset=10&seed=1";
    private static final float MAX_STALE = 60 * 60 * 24 * 30l;//过期时间为30天
    private static final String TAG = HttpClient.class.getSimpleName();
    private OkHttpClient client;
    private Context mContext;

    public HttpClient(Context context) {
        // The singleton HTTP client.
        mContext = context;
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new CacheInterceptor())
                .cache(getCache())
                .build();
    }

    public String requstHttp() throws IOException {

        Request request = new Request.Builder()
                .url(ZHIHU_ZHUANLAN_API)
                .build();

        Response response = client.newCall(request).execute();
        Log.i(TAG, "response.isSuccessful() = " + response.isSuccessful());
        String result = response.toString();
        response.close();
        return result;
    }

    public class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Response response;
            if (NetworkUtils.isNetworkAvailable(mContext)) {
                Request request = chain.request();
                response = chain.proceed(request);
                Log.i(TAG, "request.url =  " + request.url().toString());
                response.newBuilder()
                        .addHeader("Cache-Control", "public, max-age=" + 0)
                        .removeHeader("Pragma")
                        .build();
            } else {
                Request request = chain.request();
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();//没有网络，直接读取缓存
                Log.i(TAG, "request.url =  " + request.url().toString());
                response = chain.proceed(request);
                response.newBuilder()// only-if-cached完全使用缓存，如果命中失败，则返回503错误
                        .addHeader("Cache-Control", "public, only-if-cached, max-stale=" + MAX_STALE)
                        .removeHeader("Pragma")
                        .build();
            }


            return response;
        }
    }

    private Cache getCache() {
        File httpCacheDirectory = new File(SdHelper.getDiskCacheDir(), "okhttpdemo");
        int cacheSize = 10 * 1024 * 1024;//确定10M大小的缓存
        return new Cache(httpCacheDirectory, cacheSize);
    }

}
