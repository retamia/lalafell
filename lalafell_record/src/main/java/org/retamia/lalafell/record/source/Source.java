package org.retamia.lalafell.record.source;


import android.os.Handler;

import org.retamia.lalafell.record.media.Frame;

public abstract class Source {

    public abstract String getName();

    public abstract void init(Handler handler);
    public abstract void open();
    public abstract void close();
    public abstract void release();

    protected SourceListener listener = null;

    public void setListener(SourceListener listener) {
        this.listener = listener;
    }

    public interface SourceListener {

        void onImageDataAvailable(Source source, Frame image);

        void onOpenError(Source source, String reason);
    }
}
