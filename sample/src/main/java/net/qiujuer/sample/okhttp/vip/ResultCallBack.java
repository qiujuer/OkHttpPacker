package net.qiujuer.sample.okhttp.vip;

import android.util.Log;

import net.qiujuer.common.okhttp.impl.UiCallback;

/**
 * Created by qiujuer
 * on 2016/10/14.
 */
public abstract class ResultCallBack<Model> extends UiCallback<ResultBean<Model>> {

    public abstract void onRealSuccess(Model response, int code);

    @Override
    public void onSuccess(ResultBean<Model> response, int code) {
        if (response.getCode() == 1) {
            // on success
            onRealSuccess(response.getResult(), code);
        } else {
            // do same thing on error
            Log.e("ResultCallBack", "onSuccess: " + response.toString());
        }
    }
}
