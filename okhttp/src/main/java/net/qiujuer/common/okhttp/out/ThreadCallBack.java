package net.qiujuer.common.okhttp.out;

import com.squareup.okhttp.Request;

import net.qiujuer.common.okhttp.core.HttpCallback;

/**
 * Created by qiujuer
 * on 15/12/28.
 */
public abstract class ThreadCallBack<T> extends HttpCallback<T> {
    protected void dispatchBefore(final Request request) {
        onBefore(request);
    }

    protected void dispatchAfter() {
        onAfter();
    }
}
