package net.qiujuer.sample.okhttppacker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.common.okhttp.out.ThreadCallBack;
import net.qiujuer.common.okhttp.out.UiCallBack;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = Http.getSync("http://www.baidu.com");
                Log.e("MainActivity getSync0:", str);

                str = Http.getSync("http://www.baidu.com", String.class);
                Log.e("MainActivity getSync1:", str);

                str = Http.getSync("http://www.baidu.com", new ThreadCallBack<String>() {
                    @Override
                    public void onError(Request request, Response response, Exception e) {
                        Log.e("MainActivity getSync2:", "onError");
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.e("MainActivity getSync2:", "onResponse:" + response);
                    }
                });
                Log.e("MainActivity getSync2:", str);
            }
        }.start();


        Http.getAsync("http://www.baidu.com", new UiCallBack<String>() {
            @Override
            public void onError(Request request, Response response, Exception e) {
                Log.e("MainActivity", "onError");
            }

            @Override
            public void onSuccess(String response) {
                Log.e("MainActivity", response);
            }
        });


        Http.downloadPostAsync("http://down.360safe.com/setup.exe", getCacheDir().getAbsolutePath(), null,null, new HttpCallback<File>() {
            @Override
            public void onProgress(long current, long count) {
                super.onProgress(current, count);
                Log.e("MainActivity", "downloadPostAsync onProgress:" + current + "/" + count);
            }

            @Override
            public void onError(Request request, Response response, Exception e) {
                Log.e("MainActivity", "downloadPostAsync onError");
            }

            @Override
            public void onSuccess(File response) {
                Log.e("MainActivity", "downloadPostAsync onSuccess:" + response.getAbsolutePath());
            }
        });

    }
}
