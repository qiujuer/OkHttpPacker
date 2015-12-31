package net.qiujuer.common.okhttp.core;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import net.qiujuer.common.okhttp.in.FileParam;
import net.qiujuer.common.okhttp.in.StringParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;


/**
 * Created by qiujuer
 * on 15/12/28.
 */
public interface RequestBuilder {
    Request.Builder builderGet(String url, StringParam... stringParams);

    Request.Builder builderPost(String url, StringParam[] stringStringParams, FileParam[] fileParams);

    Request.Builder builderPost(String url, StringParam... stringParams);

    Request.Builder builderPost(String url, RequestBody body);

    Request.Builder builderPost(String url, String string);

    Request.Builder builderPost(String url, byte[] bytes);

    Request.Builder builderPost(String url, File file);

    Request.Builder builderPost(String url, JSONObject jsonObject);

    Request.Builder builderPost(String url, JSONArray jsonArray);

    Request.Builder builderPut(String url, StringParam... stringParams);

    Request.Builder builderDelete(String url);

    void setProtocolCharset(String protocolCharset);
}
