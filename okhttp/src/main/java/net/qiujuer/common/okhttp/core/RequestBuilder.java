/*
 * Copyright (C) 2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 1/1/2016
 * Changed 1/1/2016
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

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import net.qiujuer.common.okhttp.io.IOParam;
import net.qiujuer.common.okhttp.io.StrParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;


/**
 * This builder to build request
 */
public interface RequestBuilder {
    Request.Builder builderGet(String url, StrParam... strParams);

    Request.Builder builderPost(String url, StrParam[] stringStrParams, IOParam[] IOParams);

    Request.Builder builderPost(String url, StrParam... strParams);

    Request.Builder builderPost(String url, RequestBody body);

    Request.Builder builderPost(String url, String string);

    Request.Builder builderPost(String url, byte[] bytes);

    Request.Builder builderPost(String url, File file);

    Request.Builder builderPost(String url, JSONObject jsonObject);

    Request.Builder builderPost(String url, JSONArray jsonArray);

    Request.Builder builderPut(String url, StrParam... strParams);

    Request.Builder builderDelete(String url);

    void setProtocolCharset(String protocolCharset);
}
