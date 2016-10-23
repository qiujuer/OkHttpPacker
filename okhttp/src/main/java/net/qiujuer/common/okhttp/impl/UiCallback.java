/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
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


import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import okhttp3.Request;
import okhttp3.Response;

/**
 * This callback to call method by UI thread
 */
public abstract class UiCallback<T> extends HttpCallback<T> {
    @Override
    protected void dispatchFailure(final Request request, final Response response, final Exception e) {
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                onFailure(request, response, e);
            }
        });
    }

    @Override
    protected void dispatchSuccess(final T response, final int code) {
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                onSuccess(response, code);
            }
        });
    }

    @Override
    public void dispatchProgress(final long current, final long count) {
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                onProgress(current, count);
            }
        });
    }
}
