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

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.genius.kit.util.UiKit;

/**
 * This is http callback
 */
public abstract class HttpCallback<T> implements ProgressListener {
    protected void dispatchStart(final Request request) {
        UiKit.runOnMainThreadSync(new Runnable() {
            @Override
            public void run() {
                onStart(request);
            }
        });
    }

    protected void dispatchFinish() {
        UiKit.runOnMainThreadSync(new Runnable() {
            @Override
            public void run() {
                onFinish();
            }
        });
    }

    protected void dispatchFailure(Request request, Response response, Exception e) {
        onFailure(request, response, e);
    }

    protected void dispatchSuccess(T response, int code) {
        onSuccess(response, code);
    }

    public void dispatchProgress(long current, long count) {
        onProgress(current, count);
    }

    public void onStart(Request request) {
    }

    public void onFinish() {
    }

    public void onProgress(long current, long count) {
    }

    public abstract void onFailure(Request request, Response response, Exception e);

    public abstract void onSuccess(T response, int code);

}
