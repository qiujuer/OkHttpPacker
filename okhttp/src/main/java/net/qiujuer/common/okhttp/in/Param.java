package net.qiujuer.common.okhttp.in;


import java.io.File;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public class Param extends StringParam {
    private boolean isFile;

    public Param(String key, File file) {
        super(key, file.getAbsolutePath());
        isFile = true;
    }

    public Param(FileParam param) {
        this(param.key, param.file);
    }

    public Param(StringParam stringParam) {
        super(stringParam.key, stringParam.value);
    }

    public Param(String key, String value) {
        super(key, value);
    }

    public Param(String key, int value) {
        super(key, value);
    }

    public Param(String key, float value) {
        super(key, value);
    }

    public Param(String key, long value) {
        super(key, value);
    }

    public Param(String key, double value) {
        super(key, value);
    }

    public StringParam getStringParam() {
        return new StringParam(key, value);
    }

    public FileParam getFileParam() {
        return new FileParam(key, new File(value));
    }

    public boolean isFile() {
        return isFile;
    }
}
