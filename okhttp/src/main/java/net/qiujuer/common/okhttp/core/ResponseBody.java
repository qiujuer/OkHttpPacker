package net.qiujuer.common.okhttp.core;

import com.squareup.okhttp.MediaType;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by qiujuer
 * on 15/12/30.
 */
public class ResponseBody extends com.squareup.okhttp.ResponseBody {
    private final com.squareup.okhttp.ResponseBody mBody;
    private ProgressListener mListener;
    private BufferedSource mSource;

    public ResponseBody(com.squareup.okhttp.ResponseBody body) {
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
