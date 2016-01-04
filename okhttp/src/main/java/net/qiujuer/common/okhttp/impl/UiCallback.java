package net.qiujuer.common.okhttp.impl;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.genius.kit.util.UiKit;

/**
 * Created by qiujuer
 * on 15/12/28.
 */
public abstract class UiCallback<T> extends HttpCallback<T> {
    @Override
    protected void dispatchError(final Request request, final Response response, final Exception e) {
        UiKit.runOnMainThreadAsync(new Runnable() {
            @Override
            public void run() {
                onError(request, response, e);
            }
        });
    }

    @Override
    protected void dispatchSuccess(final T response) {
        UiKit.runOnMainThreadAsync(new Runnable() {
            @Override
            public void run() {
                onSuccess(response);
            }
        });
    }

    @Override
    public void dispatchProgress(final long current, final long count) {
        UiKit.runOnMainThreadAsync(new Runnable() {
            @Override
            public void run() {
                onProgress(current, count);
            }
        });
    }
}
