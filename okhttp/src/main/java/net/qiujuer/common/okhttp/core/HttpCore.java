/*
 * Copyright (C) 2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 1/1/2016
 * Changed 1/6/2016
 * Version 1.0.0
 * Author Qiujuer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.common.okhttp.core;

import android.text.TextUtils;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import net.qiujuer.common.okhttp.DefaultCallback;
import net.qiujuer.common.okhttp.Util;
import net.qiujuer.common.okhttp.io.IOParam;
import net.qiujuer.common.okhttp.io.StrParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is http core
 */
public class HttpCore {
    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;
    public static final int METHOD_PUT = 3;
    public static final int METHOD_DELETE = 4;

    private static final String TAG = HttpCore.class.getName();

    protected int mBufferSize = 2048;
    protected OkHttpClient mOkHttpClient;
    protected Resolver mResolver;
    protected RequestBuilder mBuilder;

    public HttpCore(Resolver resolver, RequestBuilder builder) {
        mOkHttpClient = new OkHttpClient();
        mResolver = resolver;
        mBuilder = builder;
    }

    public Resolver getResolver() {
        return mResolver;
    }

    public void setResolver(Resolver resolver) {
        mResolver = resolver;
    }

    public RequestBuilder getRequestBuilder() {
        return mBuilder;
    }

    public void setRequestBuilder(RequestBuilder builder) {
        mBuilder = builder;
    }


    // intercept the Response body stream progress
    public OkHttpClient interceptToProgressResponse() {
        mOkHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                ResponseBody body = new ForwardResponseBody(response.body());
                return response.newBuilder()
                        .body(body)
                        .build();
            }
        });
        return mOkHttpClient;
    }

    /**
     * ============Call============
     */
    protected void callStart(final HttpCallback<?> callback, final Request request) {
        if (callback == null)
            return;
        callback.dispatchStart(request);
    }

    protected void callFinish(final HttpCallback<?> callback) {
        if (callback == null)
            return;
        callback.dispatchFinish();
    }

    protected void callFailure(final HttpCallback callback, final Request request, final Response response, final Exception e) {
        Util.exception(e);
        if (callback == null)
            return;
        callback.dispatchFailure(request, response, e);
    }

    @SuppressWarnings("unchecked")
    protected void callSuccess(final HttpCallback callback, final Object object, final int code) {
        if (callback == null)
            return;

        callback.dispatchSuccess(object, code);
    }

    /**
     * ============Delivery============
     */
    protected void deliveryAsyncResult(Request request, HttpCallback<?> callback) {
        Util.log("onDelivery:" + request.url().toString());
        final HttpCallback<?> resCallBack = callback == null ? new DefaultCallback() : callback;

        //Call start
        callStart(resCallBack, request);

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                Util.log("onFailure:" + request.toString());
                callFailure(resCallBack, request, null, e);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    Object ret = null;
                    final String string = response.body().string();
                    final boolean haveValue = !TextUtils.isEmpty(string);

                    Util.log("onResponse:Code:%d Body:%s.", response.code(), (haveValue ? string : "null"));

                    if (haveValue) {
                        ret = mResolver.analysis(string, resCallBack.getClass());
                    }

                    callSuccess(resCallBack, ret, response.code());
                } catch (Exception e) {
                    Util.log("onResponse Failure:" + response.request().toString());
                    callFailure(resCallBack, response.request(), response, e);
                }
                callFinish(resCallBack);
            }
        });
    }

    protected <T> T deliveryResult(Class<T> tClass, Request request, HttpCallback<?> callback) {
        Util.log("onDelivery:" + request.url().toString());
        if (callback == null && tClass == null)
            callback = new DefaultCallback();
        final Class<?> subClass = tClass == null ? callback.getClass() : tClass;

        callStart(callback, request);
        Call call = mOkHttpClient.newCall(request);
        Response response = null;
        Object ret = null;
        try {
            response = call.execute();
            final String string = response.body().string();
            final boolean haveValue = !TextUtils.isEmpty(string);

            Util.log("onResponse:Code:%d Body:%s.", response.code(), (haveValue ? string : "null"));

            if (haveValue) {
                ret = mResolver.analysis(string, subClass);
            }

            callSuccess(callback, ret, response.code());
        } catch (Exception e) {
            Request req = response == null ? request : response.request();
            Util.log("onResponse Failure:" + req.toString());
            callFailure(callback, req, response, e);
        }
        callFinish(callback);
        return (T) ret;
    }


    protected void deliveryAsyncResult(final Request request, final StreamCall call, final HttpCallback<?> callback) {
        Util.log("onDelivery:" + request.url().toString());
        final HttpCallback<?> resCallBack = callback == null ? new DefaultCallback() : callback;

        //Call start
        callStart(resCallBack, request);

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                Util.log("onFailure:" + request.toString());
                callFailure(resCallBack, request, null, e);
            }

            @Override
            public void onResponse(final Response response) {
                OutputStream out = call.getOutputStream();
                InputStream in = null;
                byte[] buf = new byte[mBufferSize];
                try {
                    Util.log("onResponse:Code:%d Stream.", response.code());

                    ResponseBody body = response.body();
                    bindResponseProgressCallback(request.body(), body, callback);

                    in = body.byteStream();

                    int size;
                    while ((size = in.read(buf)) != -1) {
                        out.write(buf, 0, size);
                        out.flush();
                    }
                    // On success
                    call.onSuccess(response.code());
                } catch (Exception e) {
                    Util.log("onResponse Failure:" + response.request().toString());
                    callFailure(resCallBack, response.request(), response, e);
                } finally {
                    com.squareup.okhttp.internal.Util.closeQuietly(in);
                    com.squareup.okhttp.internal.Util.closeQuietly(out);
                }
                callFinish(resCallBack);
            }
        });
    }

    private void bindResponseProgressCallback(RequestBody requestBody, ResponseBody responseBody, HttpCallback<?> callback) {
        if (requestBody instanceof ForwardRequestBody) {
            if (((ForwardRequestBody) requestBody).getListener() != null) {
                return;
            }
        }
        if (responseBody instanceof ForwardResponseBody) {
            ((ForwardResponseBody) responseBody).setListener(callback);
        }
    }

    /**
     * ============Execute============
     */
    protected void executeGetAsync(HttpCallback callback, String url, Object tag, StrParam... strParams) {
        Request.Builder builder = mBuilder.builderGet(url, strParams);
        async(builder, tag, callback);
    }

    protected <T> T executeGetSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, StrParam... strParams) {
        Request.Builder builder = mBuilder.builderGet(url, strParams);
        return sync(tClass, builder, tag, callback);
    }

    protected <T> T executePostSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, StrParam... strParams) {
        Request.Builder builder = mBuilder.builderPost(url, strParams);
        return sync(tClass, builder, tag, callback);
    }

    protected <T> T executePostSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, String str) {
        Request.Builder builder = mBuilder.builderPost(url, str);
        return sync(tClass, builder, tag, callback);
    }

    protected <T> T executePostSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, RequestBody body) {
        Request.Builder builder = mBuilder.builderPost(url, body);
        return sync(tClass, builder, tag, callback);
    }

    protected <T> T executePostSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, byte[] bytes) {
        Request.Builder builder = mBuilder.builderPost(url, bytes);
        return sync(tClass, builder, tag, callback);
    }

    protected <T> T executePostSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, File file) {
        Request.Builder builder = mBuilder.builderPost(url, file);
        return sync(tClass, builder, tag, callback);
    }

    protected <T> T executePostSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, JSONObject jsonObject) {
        Request.Builder builder = mBuilder.builderPost(url, jsonObject);
        return sync(tClass, builder, tag, callback);
    }

    protected <T> T executePostSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, JSONArray jsonArray) {
        Request.Builder builder = mBuilder.builderPost(url, jsonArray);
        return sync(tClass, builder, tag, callback);
    }

    protected void executePostAsync(HttpCallback callback, String url, Object tag, StrParam... strParams) {
        Request.Builder builder = mBuilder.builderPost(url, strParams);
        async(builder, tag, callback);
    }

    protected void executePostAsync(HttpCallback callback, String url, Object tag, String str) {
        Request.Builder builder = mBuilder.builderPost(url, str);
        async(builder, tag, callback);
    }

    protected void executePostAsync(HttpCallback callback, String url, Object tag, RequestBody body) {
        Request.Builder builder = mBuilder.builderPost(url, body);
        async(builder, tag, callback);
    }

    protected void executePostAsync(HttpCallback callback, String url, Object tag, byte[] bytes) {
        Request.Builder builder = mBuilder.builderPost(url, bytes);
        async(builder, tag, callback);
    }

    protected void executePostAsync(HttpCallback callback, String url, Object tag, File file) {
        Request.Builder builder = mBuilder.builderPost(url, file);
        async(builder, tag, callback);
    }

    protected void executePostAsync(HttpCallback callback, String url, Object tag, JSONObject jsonObject) {
        Request.Builder builder = mBuilder.builderPost(url, jsonObject);
        async(builder, tag, callback);
    }

    protected void executePostAsync(HttpCallback callback, String url, Object tag, JSONArray jsonArray) {
        Request.Builder builder = mBuilder.builderPost(url, jsonArray);
        async(builder, tag, callback);
    }

    protected void executeUploadAsync(HttpCallback callback, String url, Object tag, StrParam[] strParams, IOParam... IOParams) {
        Request.Builder builder = mBuilder.builderPost(url, strParams, IOParams);
        uploadAsync(builder, tag, callback);
    }

    protected void executeDownloadAsync(HttpCallback<File> callback, String url, File file, Object tag, int method, StrParam... params) {
        Request.Builder builder;
        if (method == METHOD_POST) {
            builder = mBuilder.builderPost(url, params);
        } else {
            builder = mBuilder.builderGet(url, params);
        }
        downloadAsync(builder, file, tag, callback);
    }


    /**
     * ============Main call in this============
     */
    protected Request.Builder setTag(Request.Builder builder, Object tag) {
        if (tag != null) {
            builder.tag(tag);
        }
        return builder;
    }

    public final <T> T sync(Class<T> tClass, Request.Builder builder, Object tag, HttpCallback<T> callback) {
        setTag(builder, tag);
        return sync(tClass, builder.build(), callback);
    }

    public final void async(Request.Builder builder, Object tag, HttpCallback callback) {
        setTag(builder, tag);
        async(builder.build(), callback);
    }

    public final <T> T sync(Class<T> tClass, Request request, HttpCallback<T> callback) {
        return deliveryResult(tClass, request, callback);
    }

    public final void async(Request request, HttpCallback callback) {
        deliveryAsyncResult(request, callback);
    }

    public final <T> T uploadSync(Class<T> tClass, Request.Builder builder, Object tag, HttpCallback<T> callback) {
        setTag(builder, tag);

        Request request = builder.build();
        if (callback != null) {
            RequestBody requestBody = request.body();
            if (requestBody instanceof ForwardRequestBody) {
                ((ForwardRequestBody) (requestBody)).setListener(callback);
            }
        }
        return sync(tClass, request, callback);
    }

    public final void uploadAsync(Request.Builder builder, Object tag, HttpCallback callback) {
        setTag(builder, tag);

        Request request = builder.build();
        if (callback != null) {
            RequestBody requestBody = request.body();
            if (requestBody instanceof ForwardRequestBody) {
                ((ForwardRequestBody) (requestBody)).setListener(callback);
            }
        }
        async(request, callback);
    }

    public final void downloadAsync(Request.Builder builder, final File file, Object tag, final HttpCallback<File> callback) {
        setTag(builder, tag);
        Request request = builder.build();

        try {
            // On before crete stream, we need make new file
            Util.makeFile(file);

            final FileOutputStream out = new FileOutputStream(file);

            deliveryAsyncResult(request, new StreamCall() {
                @Override
                public OutputStream getOutputStream() {
                    return out;
                }

                @Override
                public void onSuccess(int code) {
                    callSuccess(callback, file, code);
                }
            }, callback);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callFailure(callback, request, null, e);
            callFinish(callback);
        }
    }

    // This value use to debug the log
    public static boolean DEBUG = false;
}
