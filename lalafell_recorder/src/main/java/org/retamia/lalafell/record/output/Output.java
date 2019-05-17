package org.retamia.lalafell.record.output;

import android.os.Handler;

import org.retamia.lalafell.record.media.Frame;

public abstract class Output {

    public static int FRAME_PRODUCED = 0x1001;

    public abstract void init(Handler handler);
    public abstract void open();
    public abstract void close();
    public abstract void release();
    public abstract void handleFrame(Frame frame);

    protected OutputListener listener = null;

    public void setListener(OutputListener listener) {
        this.listener = listener;
    }

    public interface OutputListener {
        void onOpenError(Output output, String reason);
    }
}
