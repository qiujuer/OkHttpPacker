package net.qiujuer.common.okhttp.io;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public class StrParam {
    public String key;
    public String value;

    public StrParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public StrParam(String key, int value) {
        this(key, String.valueOf(value));
    }

    public StrParam(String key, float value) {
        this(key, String.valueOf(value));
    }

    public StrParam(String key, long value) {
        this(key, String.valueOf(value));
    }

    public StrParam(String key, double value) {
        this(key, String.valueOf(value));
    }
}
