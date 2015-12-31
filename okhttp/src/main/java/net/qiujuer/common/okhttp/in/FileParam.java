package net.qiujuer.common.okhttp.in;

import java.io.File;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public class FileParam {
    public String key;
    public File file;

    public FileParam(String key, File file) {
        this.key = key;
        this.file = file;
    }
}
