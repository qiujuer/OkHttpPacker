package net.qiujuer.sample.okhttppacker;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.Util;
import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.common.okhttp.out.ThreadCallBack;
import net.qiujuer.common.okhttp.out.UiCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Http.enableSaveCookie(getApplication());

        final String url = "http://wthrcdn.etouch.cn/weather_mini?citykey=101010100";

        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = Http.getSync(url, String.class);
                log("getSync1:" + str);

                str = Http.getSync(url, new ThreadCallBack<String>() {
                    @Override
                    public void onError(Request request, Response response, Exception e) {
                        log("getSync2:onError");
                    }

                    @Override
                    public void onSuccess(String response) {
                        log("getSync2:onSuccess:" + response);
                    }
                });
                log("getSync2:" + str);
            }
        }.start();

        Http.getAsync(url, new UiCallBack<String>() {
            @Override
            public void onError(Request request, Response response, Exception e) {
                log("getAsync:onError");
            }

            @Override
            public void onSuccess(String response) {
                log("getAsync:onSuccess:" + response);
            }
        });

        upload();
        download();
    }

    private void download() {
        Http.downloadAsync("http://res.qiujuer.net/res/89CEC6699E00DC6093696CF7B625BAAC", getSDPath(), "download.cache", null, new HttpCallback<File>() {
            @Override
            public void onProgress(long current, long count) {
                super.onProgress(current, count);
                log("downloadAsync onProgress:" + current + "/" + count);
            }

            @Override
            public void onError(Request request, Response response, Exception e) {
                log("downloadAsync onError");
            }

            @Override
            public void onSuccess(File response) {
                log("downloadAsync onSuccess:" + response.getAbsolutePath());
            }
        });
    }

    private void upload() {
        String sd = getSDPath();
        if (sd == null)
            return;
        File file = getAssetsFile();
        if (file == null || !file.exists())
            return;
        Http.uploadAsync("http://res.qiujuer.net/res", "file", file, new HttpCallback<String>() {
            @Override
            public void onProgress(long current, long count) {
                super.onProgress(current, count);
                log("uploadAsync onProgress:" + current + "/" + count);
            }

            @Override
            public void onError(Request request, Response response, Exception e) {
                log("uploadAsync onError");
            }

            @Override
            public void onSuccess(String response) {
                log("uploadAsync onSuccess:" + response);
            }
        });
    }

    public File getAssetsFile() {
        File file = null;
        try {
            file = Util.makeFile(new File(getSDPath() + "/upload.cache"));
            InputStream is = getAssets().open("data.cache");
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while (true) {
                int len = is.read(buffer);
                if (len == -1) {
                    break;
                }
                fos.write(buffer, 0, len);
                fos.flush();
            }
            is.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public String getSDPath() {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();
            return sdDir.getAbsolutePath();
        }
        return null;
    }

    void log(String str) {
        Log.d("MAIN-LOG", str);
    }

}
