package net.qiujuer.common.okhttp.core;

import com.squareup.okhttp.MediaType;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by qiujuer
 * on 15/12/30.
 */
public class RequestBody extends com.squareup.okhttp.RequestBody {
    private final com.squareup.okhttp.RequestBody mBody;
    private ProgressListener mListener;
    private BufferedSink mSink;

    public RequestBody(com.squareup.okhttp.RequestBody body) {
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
        // We should call the sink flush to push end data
        mSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long writeCount = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);

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
