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
package net.qiujuer.common.okhttp.impl;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.Util;
import net.qiujuer.common.okhttp.core.ForwardRequestBody;
import net.qiujuer.common.okhttp.core.RequestBuilder;
import net.qiujuer.common.okhttp.io.IOParam;
import net.qiujuer.common.okhttp.io.StrParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;


/**
 * This request builder have notify status
 */
public class RequestCallBuilder implements RequestBuilder {
    /**
     * Default charset for JSON request.
     */
    protected String mProtocolCharset = "utf-8";
    // On create a new builder call to set UA and Head
    private BuilderListener mListener;

    /**
     * In this we should add same default params to the get builder
     *
     * @param sb      Get Values
     * @param isFirst The Url and values is have "?" char
     * @return values is have "?" char
     */
    protected boolean buildGetParams(StringBuilder sb, boolean isFirst) {
        BuilderListener listener = mListener;
        if (listener != null) {
            return listener.onBuildGetParams(sb, isFirst);
        } else {
            return isFirst;
        }
    }

    protected FormEncodingBuilder buildFormBody(FormEncodingBuilder formEncodingBuilder) {
        BuilderListener listener = mListener;
        if (listener != null) {
            listener.onBuildFormBody(formEncodingBuilder);
        }
        return formEncodingBuilder;
    }

    protected MultipartBuilder buildMultipartBody(MultipartBuilder multipartBuilder) {
        BuilderListener listener = mListener;
        if (listener != null) {
            listener.onBuildMultipartBody(multipartBuilder);
        }
        return multipartBuilder;
    }

    protected Request.Builder createBuilder() {
        Request.Builder builder = new Request.Builder();
        BuilderListener listener = mListener;
        if (listener != null) {
            listener.onCreateBuilder(builder);
        }
        return builder;
    }

    public void setBuilderListener(BuilderListener listener) {
        this.mListener = listener;
    }

    protected RequestBody createFormBody(StrParam... strParams) {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        formEncodingBuilder = buildFormBody(formEncodingBuilder);

        // Add values
        if (strParams != null && strParams.length > 0) {
            for (StrParam strParam : strParams) {
                if (strParam.key != null && strParam.value != null) {
                    formEncodingBuilder.add(strParam.key, strParam.value);
                    log("buildFormParam: key: " + strParam.key + " value: " + strParam.value);
                } else {
                    log("buildFormParam: key: "
                            + (strParam.key != null ? strParam.key : "null")
                            + " value: "
                            + (strParam.value != null ? strParam.value : "null"));
                }
            }
        }
        return formEncodingBuilder.build();
    }

    protected RequestBody createMultipartBody(StrParam[] stringStrParams, IOParam[] IOParams) {
        MultipartBuilder builder = new MultipartBuilder();
        builder.type(MultipartBuilder.FORM);
        builder = buildMultipartBody(builder);

        if (stringStrParams != null && stringStrParams.length > 0) {
            for (StrParam strParam : stringStrParams) {
                if (strParam.key != null && strParam.value != null) {
                    builder.addFormDataPart(strParam.key, strParam.value);
                    log("buildMultiStringParam: key: " + strParam.key + " value: " + strParam.value);
                } else {
                    log("buildMultiStringParam: key: "
                            + (strParam.key != null ? strParam.key : "null")
                            + " value: "
                            + (strParam.value != null ? strParam.value : "null"));
                }
            }
        }

        if (IOParams != null && IOParams.length > 0) {
            for (IOParam param : IOParams) {
                if (param.key != null && param.file != null) {
                    String fileName = param.file.getName();
                    RequestBody fileBody = RequestBody.create(MediaType.parse(Util.getFileMimeType(fileName)), param.file);
                    builder.addFormDataPart(param.key, fileName, fileBody);
                    log("buildMultiFileParam: key: " + param.key + " value: " + fileName);
                } else {
                    log("buildMultiFileParam: key: "
                            + (param.key != null ? param.key : "null")
                            + " file: "
                            + (param.file != null ? param.file.getName() : "null"));
                }
            }
        }
        return builder.build();
    }


    @Override
    public Request.Builder builderGet(String url, StrParam... strParams) {
        StringBuilder sb = new StringBuilder();

        // Check the url is have "?" char
        boolean isFirst = !url.contains("?");

        // Add same values
        isFirst = buildGetParams(sb, isFirst);

        // Add values
        if (strParams != null && strParams.length > 0) {
            for (StrParam strParam : strParams) {
                if (strParam.key != null && strParam.value != null) {
                    if (isFirst) {
                        isFirst = false;
                        sb.append("?");
                    } else {
                        sb.append("&");
                    }
                    sb.append(strParam.key);
                    sb.append("=");
                    sb.append(strParam.value);
                }
            }
        }

        url += sb.toString();

        Request.Builder builder = createBuilder();
        builder.url(url);
        builder.get();
        return builder;
    }

    @Override
    public Request.Builder builderPost(String url, StrParam[] stringStrParams, IOParam[] IOParams) {
        RequestBody body = createMultipartBody(stringStrParams, IOParams);
        return builderPost(url, body);
    }

    @Override
    public Request.Builder builderPost(String url, StrParam... strParams) {
        RequestBody body = createFormBody(strParams);
        return builderPost(url, body);
    }

    @Override
    public Request.Builder builderPost(String url, RequestBody body) {
        Request.Builder builder = createBuilder();
        builder.url(url);
        // In this we proxy the ForwardRequestBody to support Progress
        builder.post(new ForwardRequestBody(body));
        return builder;
    }

    @Override
    public Request.Builder builderPost(String url, String string) {
        RequestBody body = RequestBody.create(
                MediaType.parse(String.format("text/plain; charset=%s", mProtocolCharset)),
                string);

        return builderPost(url, body);
    }

    @Override
    public Request.Builder builderPost(String url, byte[] bytes) {
        RequestBody body = RequestBody.create(
                MediaType.parse(String.format("application/octet-stream; charset=%s", mProtocolCharset)),
                bytes);

        return builderPost(url, body);
    }

    @Override
    public Request.Builder builderPost(String url, File file) {
        RequestBody body = RequestBody.create(
                MediaType.parse(String.format("application/octet-stream; charset=%s", mProtocolCharset)),
                file);

        return builderPost(url, body);
    }

    @Override
    public Request.Builder builderPost(String url, JSONObject jsonObject) {
        RequestBody body = RequestBody.create(
                MediaType.parse(String.format("application/json; charset=%s", mProtocolCharset)),
                jsonObject.toString());

        return builderPost(url, body);
    }

    @Override
    public Request.Builder builderPost(String url, JSONArray jsonArray) {
        RequestBody body = RequestBody.create(
                MediaType.parse(String.format("application/json; charset=%s", mProtocolCharset)),
                jsonArray.toString());

        return builderPost(url, body);
    }

    @Override
    public Request.Builder builderPut(String url, StrParam... strParams) {
        return null;
    }

    @Override
    public Request.Builder builderDelete(String url) {
        return null;
    }

    @Override
    public void setProtocolCharset(String protocolCharset) {
        this.mProtocolCharset = protocolCharset;
    }

    void log(String str) {
        if (Http.DEBUG) {
            Log.d("RequestBuilder", str);
        }
    }

    public interface BuilderListener {
        void onCreateBuilder(Request.Builder builder);

        boolean onBuildGetParams(StringBuilder sb, boolean isFirst);

        void onBuildFormBody(FormEncodingBuilder formEncodingBuilder);

        void onBuildMultipartBody(MultipartBuilder multipartBuilder);
    }
}
