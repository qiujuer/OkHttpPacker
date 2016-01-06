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
package net.qiujuer.common.okhttp.core;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * This response body extend okhttp by progress
 */
public class ForwardResponseBody extends ResponseBody {
    private final ResponseBody mBody;
    private ProgressListener mListener;
    private BufferedSource mSource;

    public ForwardResponseBody(ResponseBody body) {
        this.mBody = body;
    }

    @Override
    public void close() throws IOException {
        this.mBody.close();
    }

    @Override
    public MediaType contentType() {
        return mBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mBody.contentLength();
    }

    public void setListener(ProgressListener listener) {
        this.mListener = listener;
    }

    public ProgressListener getListener() {
        return mListener;
    }

    @Override
    public BufferedSource source() throws IOException {
        if (mSource == null) {
            mSource = Okio.buffer(source(mBody.source()));
        }
        return mSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long readCount = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long count = super.read(sink, byteCount);
                readCount += count != -1 ? count : 0;
                ProgressListener listener = mListener;
                if (listener != null) {
                    listener.dispatchProgress(readCount, mBody.contentLength());
                }
                return count;
            }
        };
    }
}
