/*
 * Copyright (C) 2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 1/1/2016
 * Changed 1/6/2016
 * Version 1.0.0
 * Author Qiujuer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.common.okhttp;


import android.text.TextUtils;
import android.util.Log;

import net.qiujuer.common.okhttp.core.HttpCore;
import net.qiujuer.common.okhttp.io.IOParam;
import net.qiujuer.common.okhttp.io.StrParam;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is okhttp util
 */
@SuppressWarnings("ALL")
public final class Util {

    private static final String LOG_TAG = "QOK";

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

    public static StrParam[] mapToStringParams(Map<String, String> params) {
        if (params == null) return new StrParam[0];
        int size = params.size();
        if (size == 0) return new StrParam[0];
        StrParam[] res = new StrParam[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new StrParam(entry.getKey(), entry.getValue());
        }
        return res;
    }

    public static IOParam[] mapToFileParams(Map<String, File> params) {
        if (params == null) return new IOParam[0];
        int size = params.size();
        if (size == 0) return new IOParam[0];
        IOParam[] res = new IOParam[size];
        Set<Map.Entry<String, File>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, File> entry : entries) {
            res[i++] = new IOParam(entry.getKey(), entry.getValue());
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

    // Show log
    public static void log(String msg) {
        if (HttpCore.DEBUG && !TextUtils.isEmpty(msg))
            Log.d(LOG_TAG, msg);
    }

    public static void log(String fromat, Object... strs) {
        if (HttpCore.DEBUG && !TextUtils.isEmpty(fromat) && strs != null)
            Log.d(LOG_TAG, String.format(fromat, strs));
    }


    public static void log(String msg, Throwable tr) {
        if (HttpCore.DEBUG && !TextUtils.isEmpty(msg))
            Log.d(LOG_TAG, msg, tr);
    }

    // Show Error
    public static void exception(Exception e) {
        if (HttpCore.DEBUG && e != null)
            e.printStackTrace();
    }
}
