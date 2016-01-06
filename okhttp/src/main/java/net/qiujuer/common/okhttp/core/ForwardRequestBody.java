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
import com.squareup.okhttp.RequestBody;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * This request body extend okhttp by progress
 */
public class ForwardRequestBody extends RequestBody {
    private final RequestBody mBody;
    private ProgressListener mListener;

    @Override
    public long contentLength() throws IOException {
        return mBody.contentLength();
    }

    private BufferedSink mSink;

    public ForwardRequestBody(RequestBody body) {
        this.mBody = body;
    }

    @Override
    public MediaType contentType() {
        return mBody.contentType();
    }

    public void setListener(ProgressListener listener) {
        this.mListener = listener;
    }

    public ProgressListener getListener() {
        return mListener;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mSink == null) {
            mSink = Okio.buffer(sink(sink));
        }
        // Write in
        mBody.writeTo(mSink);
        // flush the buffer
        mSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long writeCount = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                // call write
                super.write(source, byteCount);

                // Length
                if (contentLength == 0) {
                    contentLength = contentLength();
                }

                // Add count
                writeCount += byteCount;
                ProgressListener listener = mListener;
                if (listener != null) {
                    listener.dispatchProgress(writeCount, mBody.contentLength());
                }
            }
        };
    }
}
