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
import android.os.SystemClock;
import android.util.Log;

import org.retamia.lalafell.record.media.Frame;
import org.retamia.lalafell.record.output.opengl.object.Image;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glEnable;


public class PreviewOutput extends Output {

    private static final String TAG = PreviewOutput.class.getName();

    private PreviewOutputHandler handler;

    private HandlerThread textureFrameAvailableHandlerThread;
    private Handler textureFrameAvailableHandler;

    private BlockingDeque<Image> images;

    public PreviewOutput(Context context) {

    }

    @Override
    public void init(Handler handler) {
        this.handler = new PreviewOutputHandler(handler.getLooper(), this);
        /*this.textureFrameAvailableHandlerThread = new HandlerThread("PreviewOutput Frame Renderer");
        this.textureFrameAvailableHandlerThread.start();
        this.textureFrameAvailableHandler = new Handler(textureFrameAvailableHandlerThread.getLooper());*/

        images = new LinkedBlockingDeque<>();
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {
        //textureFrameAvailableHandlerThread.quitSafely();
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

    private void onHandlerFrame(Frame frame) {
        Image image = new Image();
        image.setWidth(frame.getWidth());
        image.setHeight(frame.getHeight());
        image.setX(0);
        image.setY(0);
        image.setData(frame.getData());
        image.setRowStrides(frame.getRowStride());

        images.push(image);
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

            if (output == null) {
                return;
            }

            output.onHandlerFrame((Frame) msg.obj);
        }
    }



    public final static class PreviewOutputRenderer implements GLSurfaceView.Renderer {

        Rect canvasBorder = new Rect();

        private float[] projectionM;
        private float[] canvasProjectM;
        private float[] cameraM;

        private long startTime;

        private Context context;

        private Image.Shader imageShader;

        private WeakReference<PreviewOutput> previewOutputWeakReference;

        public PreviewOutputRenderer(Context context, PreviewOutput output) {
            this.context = context;
            this.previewOutputWeakReference = new WeakReference<>(output);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            // 启用混合模式
            glEnable(GL_BLEND);
            glEnable(GL_DEPTH_TEST);

            imageShader = new Image.Shader(context);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            startTime = SystemClock.uptimeMillis();

            canvasBorder.left = 0;
            canvasBorder.top = 0;
            canvasBorder.right = width;
            canvasBorder.bottom = height;

            glViewport(0, 0, width, height);

            canvasProjectM = new float[16];
            projectionM = new float[16];
            cameraM = new float[16];

            Matrix.orthoM(canvasProjectM, 0, 0.0f, canvasBorder.width(), 0.0f, canvasBorder.height(), 1.0f, 100.0f);
            Matrix.perspectiveM(projectionM, 0, 45, (float)width / height, 1.0f, 100.0f);
            Matrix.setLookAtM(cameraM, 0, 0, 0, -5, 0, 0, 0, 0, 1, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            long updateTime = SystemClock.uptimeMillis();
            float deltaTime = (updateTime - startTime) / 1000.0f;
            startTime = updateTime;

            drawObjectToCanvas(deltaTime);
            drawObjectToWorld(deltaTime);
        }

        private void drawObjectToCanvas(float deltaTime) {

            PreviewOutput output = previewOutputWeakReference.get();

            if (output == null) {
                return;
            }

            try {
                Image image = output.images.poll(1000 / 25, TimeUnit.MILLISECONDS);

                if (image == null) {
                    return;
                }

                imageShader.setProjectionM(canvasProjectM);
                imageShader.drawImage(image);
            } catch (InterruptedException e) {
                return;
            }
        }

        private void drawObjectToWorld(float deltaTime) {

        }
    }
}
