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
import com.squareup.okhttp.Response;

import net.qiujuer.genius.kit.util.UiKit;

/**
 * This is http callback
 */
public abstract class HttpCallback<T> implements ProgressListener {
    protected void dispatchBefore(final Request request) {
        UiKit.runOnMainThreadAsync(new Runnable() {
            @Override
            public void run() {
                onBefore(request);
            }
        });
    }

    protected void dispatchAfter() {
        UiKit.runOnMainThreadAsync(new Runnable() {
            @Override
            public void run() {
                onAfter();
            }
        });
    }

    protected void dispatchError(Request request, Response response, Exception e) {
        onError(request, response, e);
    }

    protected void dispatchSuccess(T response) {
        onSuccess(response);
    }

    public void dispatchProgress(long current, long count) {
        onProgress(current, count);
    }

    public void onBefore(Request request) {
    }

    public void onAfter() {
    }

    public void onProgress(long current, long count) {
    }

    public abstract void onError(Request request, Response response, Exception e);

    public abstract void onSuccess(T response);

}
