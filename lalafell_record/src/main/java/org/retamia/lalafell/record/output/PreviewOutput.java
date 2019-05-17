package org.retamia.lalafell.record.output;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.retamia.lalafell.record.media.Frame;

import java.lang.ref.WeakReference;

public class PreviewOutput extends Output implements SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = PreviewOutput.class.getName();

    private SurfaceTexture surfaceTexture;

    private PreviewOutputHandler handler;

    private HandlerThread textureFrameAvailableHandlerThread;
    private Handler textureFrameAvailableHandler;


    public PreviewOutput(Context context) {

    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
        this.surfaceTexture.setOnFrameAvailableListener(this, textureFrameAvailableHandler);
    }

    @Override
    public void init(Handler handler) {
        this.handler = new PreviewOutputHandler(handler.getLooper(), this);
        this.textureFrameAvailableHandlerThread = new HandlerThread("PreviewOutput Frame Renderer");
        this.textureFrameAvailableHandlerThread.start();
        this.textureFrameAvailableHandler = new Handler(textureFrameAvailableHandlerThread.getLooper());
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {
        textureFrameAvailableHandlerThread.quitSafely();
    }

    @Override
    public void release() {
    }

    @Override
    public void handleFrame(Frame frame) {
        Message message = Message.obtain();
        message.what = 0;
        message.obj = frame;
        handler.sendMessage(message);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }

    private void onHandlerFrame(Frame frame) {
        Log.d(TAG, "got a frame: " + frame.toString());


    }

    private static final class PreviewOutputHandler extends Handler {

        private WeakReference<PreviewOutput> reference;

        PreviewOutputHandler(Looper looper, PreviewOutput output) {
            super(looper);
            reference = new WeakReference<>(output);
        }

        @Override
        public void handleMessage(Message msg) {
            PreviewOutput output = reference.get();

            output.onHandlerFrame((Frame) msg.obj);
        }
    }
}
