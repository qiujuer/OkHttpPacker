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
package net.qiujuer.common.okhttp;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import net.qiujuer.common.okhttp.cookie.PersistentCookieStore;
import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.common.okhttp.core.HttpCore;
import net.qiujuer.common.okhttp.io.IOParam;
import net.qiujuer.common.okhttp.io.Param;
import net.qiujuer.common.okhttp.io.StrParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This is okhttp main static class
 */
public class Http extends HttpCore {
    private static Http mInstance;

    private Http() {
        super(new DefaultResolver(), new DefaultRequestBuilder());
        // ConnectTimeOut
        mOkHttpClient.setConnectTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        // To intercept the Response
        interceptToProgressResponse();
    }

    public static Http getInstance() {
        if (mInstance == null) {
            synchronized (Http.class) {
                if (mInstance == null) {
                    mInstance = new Http();
                }
            }
        }
        return mInstance;
    }

    public static void enableSaveCookie(Context context) {
        getClient().setCookieHandler(new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL));
    }

    public static void removeCookie() {
        CookieHandler handler = getClient().getCookieHandler();
        if (handler != null && handler instanceof CookieManager) {
            CookieManager manager = (CookieManager) handler;
            CookieStore store = manager.getCookieStore();
            if (store != null)
                store.removeAll();
        }
    }

    public static String getCookie() {
        CookieHandler handler = getClient().getCookieHandler();
        if (handler != null && handler instanceof CookieManager) {
            CookieManager manager = (CookieManager) handler;
            CookieStore store = manager.getCookieStore();
            if (store != null) {
                Util.log(store.toString());
                try {
                    List<HttpCookie> cookies = store.getCookies();
                    if (cookies.size() > 0) {
                        String cookieStr = "";
                        for (HttpCookie cookie : cookies) {
                            cookieStr += cookie.toString();
                        }
                        return cookieStr;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static OkHttpClient getClient() {
        return getInstance().mOkHttpClient;
    }

    public static void cancel(Object tag) {
        getClient().cancel(tag);
    }


    /**
     * ============GET SYNC===============
     */
    public static String getSync(String url) {
        return getSync(url, new StrParam[0]);
    }

    public static String getSync(String url, StrParam... strParams) {
        return getSync(String.class, url, strParams);
    }

    public static String getSync(String url, Object tag) {
        return getSync(String.class, url, tag);
    }

    public static <T> T getSync(Class<T> tClass, String url) {
        return getSync(tClass, url, new StrParam[0]);
    }

    public static <T> T getSync(Class<T> tClass, String url, StrParam... strParams) {
        return getSync(tClass, url, null, strParams);
    }

    public static <T> T getSync(Class<T> tClass, String url, List<StrParam> strParams) {
        return getSync(tClass, url, null, Util.listToParams(strParams, StrParam.class));
    }

    public static <T> T getSync(Class<T> tClass, String url, Map<String, String> params) {
        return getSync(tClass, url, null, Util.mapToStringParams(params));
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag) {
        return getSync(tClass, url, tag, new StrParam[0]);
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag, StrParam... strParams) {
        return getInstance().executeGetSync(tClass, null, url, tag, strParams);
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag, List<StrParam> strParams) {
        return getSync(tClass, url, tag, Util.listToParams(strParams, StrParam.class));
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag, Map<String, String> params) {
        return getSync(tClass, url, tag, Util.mapToStringParams(params));
    }

    public static <T> T getSync(String url, HttpCallback<T> callback) {
        return getSync(url, null, callback);
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback) {
        return getSync(url, tag, callback, new StrParam[0]);
    }

    public static <T> T getSync(String url, HttpCallback<T> callback, StrParam... strParams) {
        return getSync(url, null, callback, strParams);
    }

    public static <T> T getSync(String url, HttpCallback<T> callback, List<StrParam> strParams) {
        return getSync(url, null, callback, Util.listToParams(strParams, StrParam.class));
    }

    public static <T> T getSync(String url, HttpCallback<T> callback, Map<String, String> params) {
        return getSync(url, null, callback, Util.mapToStringParams(params));
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback, StrParam... strParams) {
        return getInstance().executeGetSync(null, callback, url, tag, strParams);
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback, List<StrParam> strParams) {
        return getSync(url, tag, callback, Util.listToParams(strParams, StrParam.class));
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback, Map<String, String> params) {
        return getSync(url, tag, callback, Util.mapToStringParams(params));
    }

    /**
     * ============GET ASYNC===============
     */
    public static void getAsync(String url, HttpCallback callback) {
        getAsync(url, callback, new StrParam[0]);
    }

    public static void getAsync(String url, HttpCallback callback, StrParam... strParams) {
        getAsync(url, null, callback, strParams);
    }

    public static void getAsync(String url, HttpCallback callback, List<StrParam> strParams) {
        getAsync(url, null, callback, Util.listToParams(strParams, StrParam.class));
    }

    public static void getAsync(String url, HttpCallback callback, Map<String, String> params) {
        getAsync(url, null, callback, Util.mapToStringParams(params));
    }

    public static void getAsync(String url, Object tag, HttpCallback callback) {
        getAsync(url, tag, callback, new StrParam[0]);
    }

    public static void getAsync(String url, Object tag, HttpCallback callback, StrParam... strParams) {
        getInstance().executeGetAsync(callback, url, tag, strParams);
    }

    public static void getAsync(String url, Object tag, HttpCallback callback, List<StrParam> strParams) {
        getAsync(url, tag, callback, Util.listToParams(strParams, StrParam.class));
    }

    public static void getAsync(String url, Object tag, HttpCallback callback, Map<String, String> params) {
        getAsync(url, tag, callback, Util.mapToStringParams(params));
    }

    /**
     * ============POST SYNC===============
     */
    public static String postSync(String url, List<StrParam> strParams) {
        return postSync(String.class, url, null, Util.listToParams(strParams, StrParam.class));
    }

    public static String postSync(String url, Map<String, String> params) {
        return postSync(String.class, url, null, Util.mapToStringParams(params));
    }

    public static String postSync(String url, StrParam... strParams) {
        return postSync(String.class, url, null, strParams);
    }

    public static <T> T postSync(Class<T> tClass, String url, List<StrParam> strParams) {
        return postSync(tClass, url, null, Util.listToParams(strParams, StrParam.class));
    }

    public static <T> T postSync(Class<T> tClass, String url, Map<String, String> params) {
        return postSync(tClass, url, null, Util.mapToStringParams(params));
    }

    public static <T> T postSync(Class<T> tClass, String url, StrParam... strParams) {
        return postSync(tClass, url, null, strParams);
    }

    public static <T> T postSync(Class<T> tClass, String url, Object tag, List<StrParam> strParams) {
        return postSync(tClass, url, tag, Util.listToParams(strParams, StrParam.class));
    }

    public static <T> T postSync(Class<T> tClass, String url, Object tag, Map<String, String> params) {
        return postSync(tClass, url, tag, Util.mapToStringParams(params));
    }

    public static <T> T postSync(Class<T> tClass, String url, Object tag, StrParam... strParams) {
        return getInstance().executeGetSync(tClass, null, url, tag, strParams);
    }

    public static <T> T postSync(String url, HttpCallback<T> callback, List<StrParam> strParams) {
        return postSync(url, null, callback, Util.listToParams(strParams, StrParam.class));
    }

    public static <T> T postSync(String url, HttpCallback<T> callback, Map<String, String> params) {
        return postSync(url, null, callback, Util.mapToStringParams(params));
    }

    public static <T> T postSync(String url, HttpCallback<T> callback, StrParam... strParams) {
        return postSync(url, null, callback, strParams);
    }

    public static <T> T postSync(String url, Object tag, HttpCallback<T> callback, List<StrParam> strParams) {
        return postSync(url, tag, callback, Util.listToParams(strParams, StrParam.class));
    }

    public static <T> T postSync(String url, Object tag, HttpCallback<T> callback, Map<String, String> params) {
        return postSync(url, tag, callback, Util.mapToStringParams(params));
    }

    public static <T> T postSync(String url, Object tag, HttpCallback<T> callback, StrParam... strParams) {
        return getInstance().executeGetSync(null, callback, url, tag, strParams);
    }

    /**
     * ============POST ASYNC===============
     */
    public static void postAsync(String url, final HttpCallback callback, List<StrParam> strParams) {
        postAsync(url, null, callback, Util.listToParams(strParams, StrParam.class));
    }

    public static void postAsync(String url, final HttpCallback callback, Map<String, String> params) {
        postAsync(url, null, callback, Util.mapToStringParams(params));
    }

    public static void postAsync(String url, final HttpCallback callback, StrParam... strParams) {
        postAsync(url, null, callback, strParams);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, List<StrParam> strParams) {
        postAsync(url, tag, callback, Util.listToParams(strParams, StrParam.class));
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, Map<String, String> params) {
        postAsync(url, tag, callback, Util.mapToStringParams(params));
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, StrParam... strParams) {
        getInstance().executePostAsync(callback, url, tag, strParams);
    }

    public static void postAsync(String url, final HttpCallback callback, RequestBody body) {
        postAsync(url, null, callback, body);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, RequestBody body) {
        getInstance().executePostAsync(callback, url, tag, body);
    }

    public static void postAsync(String url, final HttpCallback callback, String bodyStr) {
        postAsync(url, null, callback, bodyStr);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, String bodyStr) {
        getInstance().executePostAsync(callback, url, tag, bodyStr);
    }

    public static void postAsync(String url, final HttpCallback callback, byte[] bytes) {
        postAsync(url, null, callback, bytes);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, byte[] bytes) {
        getInstance().executePostAsync(callback, url, tag, bytes);
    }

    public static void postAsync(String url, final HttpCallback callback, File file) {
        postAsync(url, null, callback, file);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, File file) {
        getInstance().executePostAsync(callback, url, tag, file);
    }

    public static void postAsync(String url, final HttpCallback callback, JSONObject jsonObject) {
        postAsync(url, null, callback, jsonObject);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, JSONObject jsonObject) {
        getInstance().executePostAsync(callback, url, tag, jsonObject);
    }

    public static void postAsync(String url, final HttpCallback callback, JSONArray jsonArray) {
        postAsync(url, null, callback, jsonArray);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, JSONArray jsonArray) {
        getInstance().executePostAsync(callback, url, tag, jsonArray);
    }

    /**
     * ============UPLOAD ASYNC===============
     */

    public static void uploadAsync(String url, String key, File file, HttpCallback callback) {
        uploadAsync(url, null, callback, new IOParam(key, file));
    }

    public static void uploadAsync(String url, Object tag, String key, File file, HttpCallback callback) {
        uploadAsync(url, tag, callback, new IOParam(key, file));
    }

    public static void uploadAsync(String url, HttpCallback callback, IOParam... params) {
        uploadAsync(url, null, callback, null, params);
    }

    public static void uploadAsync(String url, Object tag, HttpCallback callback, IOParam... params) {
        uploadAsync(url, tag, callback, null, params);
    }

    public static void uploadAsync(String url, HttpCallback callback, Param... params) {
        uploadAsync(url, null, callback, params);
    }

    public static void uploadAsync(String url, Object tag, HttpCallback callback, Param... params) {
        List<IOParam> IOParams = new ArrayList<>();
        List<StrParam> stringStrParams = new ArrayList<>();
        if (params != null && params.length > 0) {
            for (Param param : params) {
                if (param.isFile()) {
                    IOParams.add(param.getFileParam());
                } else {
                    stringStrParams.add(param.getStringParam());
                }
            }
        }
        uploadAsync(url, tag, callback,
                Util.listToParams(stringStrParams, StrParam.class),
                Util.listToParams(IOParams, IOParam.class));
    }

    public static void uploadAsync(String url, HttpCallback callback, StrParam[] strParams, IOParam... IOParams) {
        uploadAsync(url, null, callback, strParams, IOParams);
    }

    public static void uploadAsync(String url, Object tag, HttpCallback callback, StrParam[] strParams, IOParam... IOParams) {
        getInstance().executeUploadAsync(callback, url, tag, strParams, IOParams);
    }

    /**
     * ============DOWNLOAD ASYNC===============
     */
    public static void downloadAsync(String url, String file, HttpCallback<File> callback) {
        downloadAsync(url, file, null, callback, new StrParam[0]);
    }

    public static void downloadAsync(String url, String file, HttpCallback<File> callback, StrParam... params) {
        downloadAsync(url, file, null, callback, params);
    }

    public static void downloadAsync(String url, String file, Object tag, HttpCallback<File> callback) {
        downloadAsync(url, file, tag, callback, new StrParam[0]);
    }

    public static void downloadAsync(String url, String file, Object tag, HttpCallback<File> callback, StrParam... params) {
        downloadAsync(url, file, tag, callback, METHOD_GET, params);
    }

    public static void downloadAsync(String url, String file, Object tag, HttpCallback<File> callback, int method, StrParam... params) {
        downloadAsync(url, new File(file), tag, callback, method, params);
    }

    public static void downloadAsync(String url, String fileDir, String fileName, Object tag, HttpCallback<File> callback, StrParam... params) {
        downloadAsync(url, fileDir, fileName, tag, callback, METHOD_GET, params);
    }

    public static void downloadAsync(String url, String fileDir, String fileName, Object tag, HttpCallback<File> callback, int method, StrParam... params) {
        downloadAsync(url, Util.getFile(fileDir, fileName, url), tag, callback, method, params);
    }

    public static void downloadAsync(String url, File outFile, Object tag, HttpCallback<File> callback, StrParam... params) {
        downloadAsync(url, outFile, tag, callback, METHOD_GET, params);
    }

    public static void downloadAsync(String url, File outFile, Object tag, HttpCallback<File> callback, int method, StrParam... params) {
        getInstance().executeDownloadAsync(callback, url, outFile, tag, method, params);
    }
}
