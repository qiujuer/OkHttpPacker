package net.qiujuer.common.okhttp.io;


import java.io.File;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public class Param extends StrParam {
    private boolean isFile;

    public Param(String key, File file) {
        super(key, file.getAbsolutePath());
        isFile = true;
    }

    public Param(IOParam param) {
        this(param.key, param.file);
    }

    public Param(StrParam strParam) {
        super(strParam.key, strParam.value);
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

    public StrParam getStringParam() {
        return new StrParam(key, value);
    }

    public IOParam getFileParam() {
        return new IOParam(key, new File(value));
    }

    public boolean isFile() {
        return isFile;
    }
}
