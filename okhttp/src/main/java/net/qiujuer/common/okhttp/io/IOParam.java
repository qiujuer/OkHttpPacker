package net.qiujuer.common.okhttp.io;

import java.io.File;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public class IOParam {
    public String key;
    public File file;

    public IOParam(String key, File file) {
        this.key = key;
        this.file = file;
    }
}
