package net.qiujuer.sample.okhttp;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.Util;
import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.common.okhttp.impl.ThreadCallback;
import net.qiujuer.common.okhttp.impl.UiCallback;
import net.qiujuer.common.okhttp.io.StrParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mUpload;
    private ProgressBar mDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUpload = (ProgressBar) findViewById(R.id.proUpload);
        mDownload = (ProgressBar) findViewById(R.id.proDownload);

        initHttp();
        get();
        //post();
        upload();
        download();
    }

    private void post() {
        String value1 = "xxx";
        String value2 = "xxx";
        String url = "http://www.baidu.com";

        Http.postAsync(url, new HttpCallback<String>() {
                    @Override
                    public void onFailure(Request request, Response response, Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(String response, int code) {
                        log(response);
                    }
                },
                new StrParam("value1", value1),
                new StrParam("value2", value2));
    }

    private void initHttp() {
        Http.DEBUG = true;
        Http.enableSaveCookie(getApplication());
    }

    private void get() {
        final String url = "http://wthrcdn.etouch.cn/weather_mini?citykey=101010100";

        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = Http.getSync(url, String.class);
                log("getSync1:" + str);

                str = Http.getSync(url, new ThreadCallback<String>() {
                    @Override
                    public void onFailure(Request request, Response response, Exception e) {
                        log("getSync2:onFailed");
                    }

                    @Override
                    public void onSuccess(String response, int code) {
                        log("getSync2:onSuccess:" + response);
                    }
                });
                log("getSync2:" + str);
            }
        }.start();

        Http.getAsync(url, new UiCallback<String>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                log("getAsync:onFailed");
            }

            @Override
            public void onSuccess(String response, int code) {
                log("getAsync:onSuccess:" + response);
            }
        });
    }

    private void download() {
        Http.downloadAsync("https://raw.githubusercontent.com/qiujuer/OkHttpPacker/master/release/sample.apk", getSDPath(), null, null, new UiCallback<File>() {
            @Override
            public void onProgress(long current, long count) {
                super.onProgress(current, count);
                log("downloadAsync onProgress:" + current + "/" + count);
                mDownload.setProgress((int) ((current * 100.00 / count)));
            }

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                e.printStackTrace();
                log("downloadAsync onFailed");
                toast("downloadAsync onFailed.");
            }

            @Override
            public void onSuccess(File response, int code) {
                log("downloadAsync onSuccess:" + response.getAbsolutePath());
                toast("downloadAsync onSuccess.");
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
        Http.uploadAsync("http://img.hoop8.com/upload.php", "uploadimg", file, new UiCallback<String>() {
            @Override
            public void onProgress(long current, long count) {
                super.onProgress(current, count);
                log("uploadAsync onProgress:" + current + "/" + count);
                mUpload.setProgress((int) ((current * 100.00 / count)));
            }

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                e.printStackTrace();
                log("uploadAsync onFailed");
                toast("uploadAsync onFailed.");
            }

            @Override
            public void onSuccess(String response, int code) {
                log("uploadAsync onSuccess:" + response);
                toast("uploadAsync onSuccess.");
            }
        });
    }

    public File getAssetsFile() {
        File file = null;
        try {
            file = Util.makeFile(new File(getSDPath() + "/upload.gif"));
            InputStream is = getAssets().open("upload.gif");
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

    void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
