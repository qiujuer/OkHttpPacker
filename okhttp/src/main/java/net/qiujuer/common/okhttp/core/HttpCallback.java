package net.qiujuer.common.okhttp.core;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.genius.kit.util.UiKit;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public abstract class HttpCallback<T> implements ProgressListener {
    protected void dispatchBefore(final Request request) {
        UiKit.runOnMainThreadSync(new Runnable() {
            @Override
            public void run() {
                onBefore(request);
            }
        });
    }

    protected void dispatchAfter() {
        UiKit.runOnMainThreadSync(new Runnable() {
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
