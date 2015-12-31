package net.qiujuer.common.okhttp;


import android.text.TextUtils;

import net.qiujuer.common.okhttp.in.FileParam;
import net.qiujuer.common.okhttp.in.StringParam;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
@SuppressWarnings("ALL")
public class Util {

    public static <T> T[] listToParams(List<T> params, Class<T> tClass) {
        if (params == null || params.size() == 0)
            return (T[]) Array.newInstance(tClass, 0);

        int size = params.size();

        try {
            T[] array = (T[]) Array.newInstance(tClass, size);
            return (T[]) params.toArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return (T[]) Array.newInstance(tClass, 0);
        }
    }

    public static StringParam[] mapToStringParams(Map<String, String> params) {
        if (params == null) return new StringParam[0];
        int size = params.size();
        if (size == 0) return new StringParam[0];
        StringParam[] res = new StringParam[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new StringParam(entry.getKey(), entry.getValue());
        }
        return res;
    }

    public static FileParam[] mapToFileParams(Map<String, File> params) {
        if (params == null) return new FileParam[0];
        int size = params.size();
        if (size == 0) return new FileParam[0];
        FileParam[] res = new FileParam[size];
        Set<Map.Entry<String, File>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, File> entry : entries) {
            res[i++] = new FileParam(entry.getKey(), entry.getValue());
        }
        return res;
    }

    public static String getFileMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static File getFile(String fileDir, String fileName, String url) {
        // check the file dir
        if (TextUtils.isEmpty(fileDir))
            throw new NullPointerException("File Dir is not null.");

        // make dir
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // check the file name
        if (TextUtils.isEmpty(fileName)) {
            int separatorIndex = url.lastIndexOf("/");
            fileName = (separatorIndex < 0) ? url : url.substring(separatorIndex + 1, url.length());
            if (TextUtils.isEmpty(fileName) || !fileName.contains("."))
                fileName = String.valueOf(System.currentTimeMillis()) + ".cache";
        }

        return new File(dir, fileName);
    }

    public static File makeFile(File file) {
        if (file.exists()) {
            file.delete();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
