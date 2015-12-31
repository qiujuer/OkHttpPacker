package net.qiujuer.common.okhttp.in;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public class StringParam {
    public String key;
    public String value;

    public StringParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public StringParam(String key, int value) {
        this(key, String.valueOf(value));
    }

    public StringParam(String key, float value) {
        this(key, String.valueOf(value));
    }

    public StringParam(String key, long value) {
        this(key, String.valueOf(value));
    }

    public StringParam(String key, double value) {
        this(key, String.valueOf(value));
    }
}
