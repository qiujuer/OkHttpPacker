package net.qiujuer.common.okhttp;

import android.util.Log;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.out.ThreadCallBack;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public class DefaultCallback extends ThreadCallBack<String> {
    @Override
    public void onError(Request request, Response response, Exception e) {
        Log.d("Callback", "onError.");
    }

    @Override
    public void onSuccess(String response) {
        Log.d("Callback", "onSuccess:" + response);
    }

    @Override
    public void onProgress(long current, long count) {
        Log.d("Callback", "onProgress:" + current + "/" + count);
    }
}
