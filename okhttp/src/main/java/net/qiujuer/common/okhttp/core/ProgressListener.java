package net.qiujuer.common.okhttp.core;

/**
 * Created by qiujuer
 * on 15/12/30.
 */
public interface ProgressListener {
    void dispatchProgress(long current, long count);
}
