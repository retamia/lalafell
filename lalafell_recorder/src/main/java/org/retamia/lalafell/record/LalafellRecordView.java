package org.retamia.lalafell.record;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.retamia.lalafell.record.output.MediaCodecOutput;
import org.retamia.lalafell.record.output.PreviewOutput;
import org.retamia.lalafell.record.scene.Scene;
import org.retamia.lalafell.record.scene.SceneListener;
import org.retamia.lalafell.record.source.Source;
import org.retamia.lalafell.record.source.CameraSource;

public class LalafellRecordView extends FrameLayout {

    private GLSurfaceView glSurfaceView;
    private Scene defaultScene;

    private CameraSource cameraSource;

    private PreviewOutput previewOutput;
    private PreviewOutput.PreviewOutputRenderer previewOutputRenderer;
    private MediaCodecOutput mediaCodecOutput;

    public LalafellRecordView(@NonNull Context context) {
        super(context);
    }

    public LalafellRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LalafellRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LalafellRecordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init() {
        cameraSource = new CameraSource(getContext());
        previewOutput = new PreviewOutput(getContext());
        mediaCodecOutput = new MediaCodecOutput(getContext());

        defaultScene = new Scene(getContext());
        defaultScene.addSource(cameraSource);
        defaultScene.addOutput(previewOutput);

        defaultScene.setListener(sceneListener);
        defaultScene.init();

        glSurfaceView = new GLSurfaceView(getContext());
        glSurfaceView.setEGLContextClientVersion(2);
        previewOutputRenderer = new PreviewOutput.PreviewOutputRenderer();
        glSurfaceView.setRenderer(previewOutputRenderer);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);
        glSurfaceView.setLayoutParams(lp);
        addView(glSurfaceView);
    }

    public void pause() {
        glSurfaceView.onPause();
    }

    public void resume() {
        glSurfaceView.onResume();
    }

    public void release() {
        defaultScene.release();
    }

    private SceneListener sceneListener = new SceneListener() {
        @Override
        public void onSourceOpenError(Source source) {
            Toast.makeText(LalafellRecordView.this.getContext(), String.format("%s输入源打开错误", source.getName()), Toast.LENGTH_LONG).show();
        }
    };
}
