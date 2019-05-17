package org.retamia.lalafell.record.output;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.retamia.lalafell.record.media.Frame;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glBlendFunc;

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

    //@TODO frame data is draw canvas layer
    @Override
    public void handleFrame(Frame frame) {
        Message message = Message.obtain();
        message.what = 0;
        message.obj = frame;
        handler.sendMessage(message);
    }

    //@TODO renderer 3d obj;
    //public void handler3DObject()

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



    public final static class PreviewOutputRenderer implements GLSurfaceView.Renderer {

        Rect canvansBorder = new Rect();

        private float[] projectionM;
        private float[] canvansProjectM;
        private float[] cameraM;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            // 启用混合模式
            glEnable(GL_BLEND);
            glEnable(GL_DEPTH_TEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            canvansBorder.left = 0;
            canvansBorder.top = 0;
            canvansBorder.right = width;
            canvansBorder.bottom = height;

            glViewport(0, 0, width, height);

            canvansProjectM = new float[16];
            projectionM = new float[16];
            cameraM = new float[16];

            Matrix.orthoM(canvansProjectM, 0, 0.0f, canvansBorder.width(), 0.0f, canvansBorder.height(), 1.0f, 100.0f);
            Matrix.perspectiveM(projectionM, 0, 45, (float)width / height, 1.0f, 100.0f);
            Matrix.setLookAtM(cameraM, 0, 0, 0, -5, 0, 0, 0, 0, 1, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        }
    }
}
