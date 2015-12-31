package net.qiujuer.common.okhttp.core;

import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.internal.Util;

import net.qiujuer.common.okhttp.DefaultCallback;
import net.qiujuer.common.okhttp.in.FileParam;
import net.qiujuer.common.okhttp.in.StringParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by qiujuer
 * on 15/12/28.
 */
public class HttpCore {
    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;
    public static final int METHOD_PUT = 3;
    public static final int METHOD_DELETE = 4;

    private static final String TAG = HttpCore.class.getName();

    protected OkHttpClient mOkHttpClient;
    protected Resolver mResolver;
    protected RequestBuilder mBuilder;

    public OkHttpClient interceptToProgressResponse(OkHttpClient client) {
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new net.qiujuer.common.okhttp.core.ResponseBody(originalResponse.body()))
                        .build();
            }
        });
        return client;
    }


    /**
     * ============Call============
     */
    protected void callBefore(final HttpCallback<?> callback, final Request request) {
        if (callback == null)
            return;
        callback.dispatchBefore(request);
    }

    protected void callAfter(final HttpCallback<?> callback) {
        if (callback == null)
            return;
        callback.dispatchAfter();
    }

    protected void callFailed(final HttpCallback callback, final Request request, final Response response, final Exception e) {
        exception(e);
        if (callback == null)
            return;
        callback.dispatchError(request, response, e);
    }

    @SuppressWarnings("unchecked")
    protected void callSuccess(final HttpCallback callback, final Object object) {
        if (callback == null)
            return;

        callback.dispatchSuccess(object);
    }

    /**
     * ============Delivery============
     */
    protected void deliveryAsyncResult(Request request, HttpCallback<?> callback) {
        final HttpCallback<?> resCallBack = callback == null ? new DefaultCallback() : callback;

        //Call start
        callBefore(resCallBack, request);

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                log("onFailure:" + request.toString());
                callFailed(resCallBack, request, null, e);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    final String string = response.body().string();
                    log("onResponse:" + string);

                    Object ret = mResolver.analysis(string, resCallBack.getClass());

                    callSuccess(resCallBack, ret);
                } catch (IOException | com.google.gson.JsonParseException e) {
                    log("onResponse Failure:" + response.request().toString());
                    callFailed(resCallBack, response.request(), response, e);
                }
                callAfter(resCallBack);
            }
        });
    }

    protected <T> T deliveryResult(Class<T> tClass, Request request, HttpCallback<?> callback) {
        if (callback == null && tClass == null)
            callback = new DefaultCallback();
        final Class<?> subClass = tClass == null ? callback.getClass() : tClass;

        callBefore(callback, request);
        Call call = mOkHttpClient.newCall(request);
        Response response = null;
        Object ret = null;
        try {
            response = call.execute();
            String string = response.body().string();
            log("onResponse:" + string);

            ret = mResolver.analysis(string, subClass);

            callSuccess(callback, ret);

        } catch (IOException | com.google.gson.JsonParseException e) {
            Request req = response == null ? request : response.request();
            log("onResponse Failure:" + req.toString());
            callFailed(callback, req, response, e);
        }
        callAfter(callback);
        return (T) ret;
    }


    protected void deliveryAsyncResult(final Request request, final StreamCall call, final HttpCallback<?> callback) {
        final HttpCallback<?> resCallBack = callback == null ? new DefaultCallback() : callback;

        //Call start
        callBefore(resCallBack, request);

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                log("onFailure:" + request.toString());
                callFailed(resCallBack, request, null, e);
            }

            @Override
            public void onResponse(final Response response) {
                OutputStream out = call.getOutputStream();
                InputStream in = null;
                byte[] buf = new byte[4096];
                try {
                    log("onResponse:Stream.");

                    ResponseBody body = response.body();
                    bindResponseProgressCallback(request.body(), body, callback);

                    in = body.byteStream();

                    int size;
                    while ((size = in.read(buf)) != -1) {
                        out.write(buf, 0, size);
                        out.flush();
                    }
                    // On success
                    call.onSuccess();
                } catch (IOException e) {
                    log("onResponse Failure:" + response.request().toString());
                    callFailed(resCallBack, response.request(), response, e);
                } finally {
                    Util.closeQuietly(in);
                    Util.closeQuietly(out);
                }
                callAfter(resCallBack);
            }
        });
    }

    private void bindResponseProgressCallback(RequestBody requestBody, ResponseBody responseBody, HttpCallback<?> callback) {
        if (requestBody instanceof net.qiujuer.common.okhttp.core.RequestBody) {
            if (((net.qiujuer.common.okhttp.core.RequestBody) requestBody).getListener() != null) {
                return;
            }
        }
        if (responseBody instanceof net.qiujuer.common.okhttp.core.ResponseBody) {
            ((net.qiujuer.common.okhttp.core.ResponseBody) responseBody).setListener(callback);
        }
    }

    /**
     * ============Execute============
     */
    protected void executeGetAsync(HttpCallback callback, String url, Object tag, StringParam... stringParams) {
        Request.Builder builder = mBuilder.builderGet(url, stringParams);
        async(builder, tag, callback);
    }

    protected <T> T executeGetSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, StringParam... stringParams) {
        Request.Builder builder = mBuilder.builderGet(url, stringParams);
        return sync(tClass, builder, tag, callback);
    }

    protected <T> T executePostSync(Class<T> tClass, HttpCallback<T> callback, String url, Object tag, StringParam... stringParams) {
        Request.Builder builder = mBuilder.builderPost(url, stringParams);
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

    protected void executePostAsync(HttpCallback callback, String url, Object tag, StringParam... stringParams) {
        Request.Builder builder = mBuilder.builderPost(url, stringParams);
        async(builder, tag, callback);
    }

    protected void executePostAsync(HttpCallback callback, String url, Object tag, String str) {
        Request.Builder builder = mBuilder.builderPost(url, str);
        async(builder, tag, callback);
    }

    protected void executePostAsync(HttpCallback callback, String url, Object tag, com.squareup.okhttp.RequestBody body) {
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

    protected void executeUploadAsync(HttpCallback callback, String url, Object tag, StringParam[] stringParams, FileParam... fileParams) {
        Request.Builder builder = mBuilder.builderPost(url, stringParams, fileParams);
        uploadAsync(builder, tag, callback);
    }

    protected void executeDownloadAsync(HttpCallback<File> callback, String url, File file, Object tag, int method, StringParam... params) {
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
            com.squareup.okhttp.RequestBody requestBody = request.body();
            if (requestBody instanceof net.qiujuer.common.okhttp.core.RequestBody) {
                ((net.qiujuer.common.okhttp.core.RequestBody) (requestBody)).setListener(callback);
            }
        }
        return sync(tClass, request, callback);
    }

    public final void uploadAsync(Request.Builder builder, Object tag, HttpCallback callback) {
        setTag(builder, tag);

        Request request = builder.build();
        if (callback != null) {
            com.squareup.okhttp.RequestBody requestBody = request.body();
            if (requestBody instanceof net.qiujuer.common.okhttp.core.RequestBody) {
                ((net.qiujuer.common.okhttp.core.RequestBody) (requestBody)).setListener(callback);
            }
        }
        async(request, callback);
    }

    public final void downloadAsync(Request.Builder builder, final File file, Object tag, final HttpCallback<File> callback) {
        setTag(builder, tag);
        Request request = builder.build();

        try {
            // On before crete stream, we need make new file
            net.qiujuer.common.okhttp.Util.makeFile(file);

            final FileOutputStream out = new FileOutputStream(file);

            deliveryAsyncResult(request, new StreamCall() {
                @Override
                public OutputStream getOutputStream() {
                    return out;
                }

                @Override
                public void onSuccess() {
                    callSuccess(callback, file);
                }
            }, callback);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callFailed(callback, request, null, e);
            callAfter(callback);
        }
    }


    // This value use to debug the log
    public static boolean DEBUG = true;

    // Show log
    protected static void log(String str) {
        if (DEBUG && !TextUtils.isEmpty(str))
            Log.d(TAG, str);
    }

    // Show Error
    protected static void exception(Exception e) {
        if (DEBUG && e != null)
            e.printStackTrace();
    }
}
