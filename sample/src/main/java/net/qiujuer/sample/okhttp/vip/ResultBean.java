package net.qiujuer.sample.okhttp.vip;

/**
 * Created by qiujuer
 * on 2016/10/14.
 */
public class ResultBean<T> {
    private int code;
    private String message;
    private String time;
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", result=" + result +
                '}';
    }
}
