package net.qiujuer.common.okhttp.core;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.Util;
import net.qiujuer.common.okhttp.in.FileParam;
import net.qiujuer.common.okhttp.in.StringParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;


/**
 * Created by qiujuer
 * on 15/12/28.
 */
public class RequestListenerBuilder implements RequestBuilder {
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

    protected RequestBody createFormBody(StringParam... stringParams) {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        formEncodingBuilder = buildFormBody(formEncodingBuilder);

        // Add values
        if (stringParams != null && stringParams.length > 0) {
            for (StringParam stringParam : stringParams) {
                if (stringParam.key != null && stringParam.value != null) {
                    formEncodingBuilder.add(stringParam.key, stringParam.value);
                    log("buildFormParam: key: " + stringParam.key + " value: " + stringParam.value);
                } else {
                    log("buildFormParam: key: "
                            + (stringParam.key != null ? stringParam.key : "null")
                            + " value: "
                            + (stringParam.value != null ? stringParam.value : "null"));
                }
            }
        }
        return formEncodingBuilder.build();
    }

    protected RequestBody createMultipartBody(StringParam[] stringStringParams, FileParam[] fileParams) {
        MultipartBuilder builder = new MultipartBuilder();
        builder.type(MultipartBuilder.FORM);
        builder = buildMultipartBody(builder);

        if (stringStringParams != null && stringStringParams.length > 0) {
            for (StringParam stringParam : stringStringParams) {
                if (stringParam.key != null && stringParam.value != null) {
                    builder.addFormDataPart(stringParam.key, stringParam.value);
                    log("buildMultiStringParam: key: " + stringParam.key + " value: " + stringParam.value);
                } else {
                    log("buildMultiStringParam: key: "
                            + (stringParam.key != null ? stringParam.key : "null")
                            + " value: "
                            + (stringParam.value != null ? stringParam.value : "null"));
                }
            }
        }

        if (fileParams != null && fileParams.length > 0) {
            for (FileParam param : fileParams) {
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
    public Request.Builder builderGet(String url, StringParam... stringParams) {
        StringBuilder sb = new StringBuilder();

        // Check the url is have "?" char
        boolean isFirst = !url.contains("?");

        // Add same values
        isFirst = buildGetParams(sb, isFirst);

        // Add values
        if (stringParams != null && stringParams.length > 0) {
            for (StringParam stringParam : stringParams) {
                if (stringParam.key != null && stringParam.value != null) {
                    if (isFirst) {
                        isFirst = false;
                        sb.append("?");
                    } else {
                        sb.append("&");
                    }
                    sb.append(stringParam.key);
                    sb.append("=");
                    sb.append(stringParam.value);
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
    public Request.Builder builderPost(String url, StringParam[] stringStringParams, FileParam[] fileParams) {
        RequestBody body = createMultipartBody(stringStringParams, fileParams);
        return builderPost(url, body);
    }

    @Override
    public Request.Builder builderPost(String url, StringParam... stringParams) {
        RequestBody body = createFormBody(stringParams);
        return builderPost(url, body);
    }

    @Override
    public Request.Builder builderPost(String url, RequestBody body) {
        Request.Builder builder = createBuilder();
        builder.url(url);
        // In this we proxy the RequestBody to support Progress
        builder.post(new net.qiujuer.common.okhttp.core.RequestBody(body));
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
    public Request.Builder builderPut(String url, StringParam... stringParams) {
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
