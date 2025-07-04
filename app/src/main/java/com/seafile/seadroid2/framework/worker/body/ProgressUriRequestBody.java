package com.seafile.seadroid2.framework.worker.body;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.seafile.seadroid2.framework.util.SLogs;
import com.seafile.seadroid2.framework.worker.TransferWorker;
import com.seafile.seadroid2.listener.FileTransferProgressListener;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressUriRequestBody extends RequestBody {

    private final Uri uri;
    private final MediaType mediaType;
    private final FileTransferProgressListener fileTransferProgressListener;
    private final Context context;
    private final long estimationSize;
    private boolean isStop = false;

    public ProgressUriRequestBody(Context context, Uri uri, long size, FileTransferProgressListener fileTransferProgressListener) {
        this.context = context;
        this.uri = uri;
        this.mediaType = MediaType.parse("application/octet-stream");
        this.fileTransferProgressListener = fileTransferProgressListener;
        this.estimationSize = size;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }

    /**
     * -1: chunked.
     * We can't calculate the data length of a stream.
     * Here, we use the chunk mode, and the data length is unknown.
     */
    @Override
    public long contentLength() throws IOException {
        return super.contentLength();
    }

    public long temp = System.currentTimeMillis();

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            long current = 0;

            if (inputStream != null) {
                byte[] buffer = new byte[TransferWorker.SEGMENT_SIZE];
                int readCount;
                while ((readCount = inputStream.read(buffer)) != -1) {

                    if (Thread.currentThread().isInterrupted()) {
                        SLogs.e(new InterruptedException("Upload canceled"));
                        return;
                    }

                    if (isStop) {
                        return;
                    }


                    sink.write(buffer, 0, readCount);

                    current += readCount;

                    long nowt = System.currentTimeMillis();
                    // 1s refresh progress
                    if (nowt - temp >= 1000) {
                        temp = nowt;
                        if (fileTransferProgressListener != null) {
                            fileTransferProgressListener.onProgressNotify(current, estimationSize);
                        }
                    }
                }

                //notify complete
                if (fileTransferProgressListener != null) {
                    fileTransferProgressListener.onProgressNotify(estimationSize, estimationSize);
                }
            }
        } catch (IOException e) {
            SLogs.e(e);
            throw e;
        }
    }
}
