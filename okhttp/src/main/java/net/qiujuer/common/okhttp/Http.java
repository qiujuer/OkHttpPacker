package net.qiujuer.common.okhttp;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.common.okhttp.core.HttpCore;
import net.qiujuer.common.okhttp.core.Resolver;
import net.qiujuer.common.okhttp.in.FileParam;
import net.qiujuer.common.okhttp.in.Param;
import net.qiujuer.common.okhttp.in.StringParam;

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
 * Created by qiujuer
 * on 15/12/25.
 */
public class Http extends HttpCore {
    private static Http mInstance;

    private Http() {
        super();
        mOkHttpClient = new OkHttpClient();
        // ConnectTimeOut
        mOkHttpClient.setConnectTimeout(10 * 1000, TimeUnit.MILLISECONDS);
        mResolver = new DefaultResolver();
        mBuilder = new DefaultRequestBuilder();
        mOkHttpClient = interceptToProgressResponse(mOkHttpClient);
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
                log(store.toString());
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

    public static void setResolver(Resolver resolver) {
        getInstance().mResolver = resolver;
    }

    public static OkHttpClient getClient() {
        return getInstance().mOkHttpClient;
    }


    /**
     * ============GET SYNC===============
     */
    public static String getSync(String url) {
        return getSync(url, new StringParam[0]);
    }

    public static String getSync(String url, StringParam... stringParams) {
        return getSync(String.class, url, stringParams);
    }

    public static String getSync(String url, Object tag) {
        return getSync(String.class, url, tag);
    }

    public static <T> T getSync(Class<T> tClass, String url) {
        return getSync(tClass, url, new StringParam[0]);
    }

    public static <T> T getSync(Class<T> tClass, String url, StringParam... stringParams) {
        return getSync(tClass, url, null, stringParams);
    }

    public static <T> T getSync(Class<T> tClass, String url, List<StringParam> stringParams) {
        return getSync(tClass, url, null, Util.listToParams(stringParams, StringParam.class));
    }

    public static <T> T getSync(Class<T> tClass, String url, Map<String, String> params) {
        return getSync(tClass, url, null, Util.mapToStringParams(params));
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag) {
        return getSync(tClass, url, tag, new StringParam[0]);
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag, StringParam... stringParams) {
        return getInstance().executeGetSync(tClass, null, url, tag, stringParams);
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag, List<StringParam> stringParams) {
        return getSync(tClass, url, tag, Util.listToParams(stringParams, StringParam.class));
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag, Map<String, String> params) {
        return getSync(tClass, url, tag, Util.mapToStringParams(params));
    }

    public static <T> T getSync(String url, HttpCallback<T> callback) {
        return getSync(url, null, callback);
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback) {
        return getSync(url, tag, callback, new StringParam[0]);
    }

    public static <T> T getSync(String url, HttpCallback<T> callback, StringParam... stringParams) {
        return getSync(url, null, callback, stringParams);
    }

    public static <T> T getSync(String url, HttpCallback<T> callback, List<StringParam> stringParams) {
        return getSync(url, null, callback, Util.listToParams(stringParams, StringParam.class));
    }

    public static <T> T getSync(String url, HttpCallback<T> callback, Map<String, String> params) {
        return getSync(url, null, callback, Util.mapToStringParams(params));
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback, StringParam... stringParams) {
        return getInstance().executeGetSync(null, callback, url, tag, stringParams);
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback, List<StringParam> stringParams) {
        return getSync(url, tag, callback, Util.listToParams(stringParams, StringParam.class));
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback, Map<String, String> params) {
        return getSync(url, tag, callback, Util.mapToStringParams(params));
    }

    /**
     * ============GET ASYNC===============
     */
    public static void getAsync(String url, HttpCallback callback) {
        getAsync(url, callback, new StringParam[0]);
    }

    public static void getAsync(String url, HttpCallback callback, StringParam... stringParams) {
        getAsync(url, null, callback, stringParams);
    }

    public static void getAsync(String url, HttpCallback callback, List<StringParam> stringParams) {
        getAsync(url, null, callback, Util.listToParams(stringParams, StringParam.class));
    }

    public static void getAsync(String url, HttpCallback callback, Map<String, String> params) {
        getAsync(url, null, callback, Util.mapToStringParams(params));
    }

    public static void getAsync(String url, Object tag, HttpCallback callback) {
        getAsync(url, tag, callback, new StringParam[0]);
    }

    public static void getAsync(String url, Object tag, HttpCallback callback, StringParam... stringParams) {
        getInstance().executeGetAsync(callback, url, tag, stringParams);
    }

    public static void getAsync(String url, Object tag, HttpCallback callback, List<StringParam> stringParams) {
        getAsync(url, tag, callback, Util.listToParams(stringParams, StringParam.class));
    }

    public static void getAsync(String url, Object tag, HttpCallback callback, Map<String, String> params) {
        getAsync(url, tag, callback, Util.mapToStringParams(params));
    }

    /**
     * ============POST SYNC===============
     */
    public static String postSync(String url, List<StringParam> stringParams) {
        return postSync(String.class, url, null, Util.listToParams(stringParams, StringParam.class));
    }

    public static String postSync(String url, Map<String, String> params) {
        return postSync(String.class, url, null, Util.mapToStringParams(params));
    }

    public static String postSync(String url, StringParam... stringParams) {
        return postSync(String.class, url, null, stringParams);
    }

    public static <T> T postSync(Class<T> tClass, String url, List<StringParam> stringParams) {
        return postSync(tClass, url, null, Util.listToParams(stringParams, StringParam.class));
    }

    public static <T> T postSync(Class<T> tClass, String url, Map<String, String> params) {
        return postSync(tClass, url, null, Util.mapToStringParams(params));
    }

    public static <T> T postSync(Class<T> tClass, String url, StringParam... stringParams) {
        return postSync(tClass, url, null, stringParams);
    }

    public static <T> T postSync(Class<T> tClass, String url, Object tag, List<StringParam> stringParams) {
        return postSync(tClass, url, tag, Util.listToParams(stringParams, StringParam.class));
    }

    public static <T> T postSync(Class<T> tClass, String url, Object tag, Map<String, String> params) {
        return postSync(tClass, url, tag, Util.mapToStringParams(params));
    }

    public static <T> T postSync(Class<T> tClass, String url, Object tag, StringParam... stringParams) {
        return getInstance().executeGetSync(tClass, null, url, tag, stringParams);
    }

    public static <T> T postSync(String url, HttpCallback<T> callback, List<StringParam> stringParams) {
        return postSync(url, null, callback, Util.listToParams(stringParams, StringParam.class));
    }

    public static <T> T postSync(String url, HttpCallback<T> callback, Map<String, String> params) {
        return postSync(url, null, callback, Util.mapToStringParams(params));
    }

    public static <T> T postSync(String url, HttpCallback<T> callback, StringParam... stringParams) {
        return postSync(url, null, callback, stringParams);
    }

    public static <T> T postSync(String url, Object tag, HttpCallback<T> callback, List<StringParam> stringParams) {
        return postSync(url, tag, callback, Util.listToParams(stringParams, StringParam.class));
    }

    public static <T> T postSync(String url, Object tag, HttpCallback<T> callback, Map<String, String> params) {
        return postSync(url, tag, callback, Util.mapToStringParams(params));
    }

    public static <T> T postSync(String url, Object tag, HttpCallback<T> callback, StringParam... stringParams) {
        return getInstance().executeGetSync(null, callback, url, tag, stringParams);
    }

    /**
     * ============POST ASYNC===============
     */
    public static void postAsync(String url, final HttpCallback callback, List<StringParam> stringParams) {
        postAsync(url, null, callback, Util.listToParams(stringParams, StringParam.class));
    }

    public static void postAsync(String url, final HttpCallback callback, Map<String, String> params) {
        postAsync(url, null, callback, Util.mapToStringParams(params));
    }

    public static void postAsync(String url, final HttpCallback callback, StringParam... stringParams) {
        postAsync(url, null, callback, stringParams);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, List<StringParam> stringParams) {
        postAsync(url, tag, callback, Util.listToParams(stringParams, StringParam.class));
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, Map<String, String> params) {
        postAsync(url, tag, callback, Util.mapToStringParams(params));
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, StringParam... stringParams) {
        getInstance().executePostAsync(callback, url, tag, stringParams);
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
        uploadAsync(url, null, callback, new FileParam(key, file));
    }

    public static void uploadAsync(String url, Object tag, String key, File file, HttpCallback callback) {
        uploadAsync(url, tag, callback, new FileParam(key, file));
    }

    public static void uploadAsync(String url, HttpCallback callback, FileParam... params) {
        uploadAsync(url, null, callback, null, params);
    }

    public static void uploadAsync(String url, Object tag, HttpCallback callback, FileParam... params) {
        uploadAsync(url, tag, callback, null, params);
    }

    public static void uploadAsync(String url, HttpCallback callback, Param... params) {
        uploadAsync(url, null, callback, params);
    }

    public static void uploadAsync(String url, Object tag, HttpCallback callback, Param... params) {
        List<FileParam> fileParams = new ArrayList<>();
        List<StringParam> stringStringParams = new ArrayList<>();
        if (params != null && params.length > 0) {
            for (Param param : params) {
                if (param.isFile()) {
                    fileParams.add(param.getFileParam());
                } else {
                    stringStringParams.add(param.getStringParam());
                }
            }
        }
        uploadAsync(url, tag, callback,
                Util.listToParams(stringStringParams, StringParam.class),
                Util.listToParams(fileParams, FileParam.class));
    }

    public static void uploadAsync(String url, HttpCallback callback, StringParam[] stringParams, FileParam... fileParams) {
        uploadAsync(url, null, callback, stringParams, fileParams);
    }

    public static void uploadAsync(String url, Object tag, HttpCallback callback, StringParam[] stringParams, FileParam... fileParams) {
        getInstance().executeUploadAsync(callback, url, tag, stringParams, fileParams);
    }

    /**
     * ============DOWNLOAD ASYNC===============
     */
    public static void downloadAsync(String url, String file, HttpCallback<File> callback) {
        downloadAsync(url, file, null, callback, new StringParam[0]);
    }

    public static void downloadAsync(String url, String file, HttpCallback<File> callback, StringParam... params) {
        downloadAsync(url, file, null, callback, params);
    }

    public static void downloadAsync(String url, String file, Object tag, HttpCallback<File> callback) {
        downloadAsync(url, file, tag, callback, new StringParam[0]);
    }

    public static void downloadAsync(String url, String file, Object tag, HttpCallback<File> callback, StringParam... params) {
        downloadAsync(url, file, tag, callback, METHOD_GET, params);
    }

    public static void downloadAsync(String url, String file, Object tag, HttpCallback<File> callback, int method, StringParam... params) {
        downloadAsync(url, new File(file), tag, callback, method, params);
    }

    public static void downloadAsync(String url, String fileDir, String fileName, Object tag, HttpCallback<File> callback, StringParam... params) {
        downloadAsync(url, fileDir, fileName, tag, callback, METHOD_GET, params);
    }

    public static void downloadAsync(String url, String fileDir, String fileName, Object tag, HttpCallback<File> callback, int method, StringParam... params) {
        downloadAsync(url, Util.getFile(fileDir, fileName, url), tag, callback, method, params);
    }

    public static void downloadAsync(String url, File outFile, Object tag, HttpCallback<File> callback, StringParam... params) {
        downloadAsync(url, outFile, tag, callback, METHOD_GET, params);
    }

    public static void downloadAsync(String url, File outFile, Object tag, HttpCallback<File> callback, int method, StringParam... params) {
        getInstance().executeDownloadAsync(callback, url, outFile, tag, method, params);
    }
}
