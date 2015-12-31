package net.qiujuer.common.okhttp.core;

import java.io.OutputStream;

/**
 * Created by qiujuer
 * on 15/12/31.
 */
public interface StreamCall {
    OutputStream getOutputStream();

    void onSuccess();
}
