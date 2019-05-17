package org.retamia.lalafell.record;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
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

public class LalafellRecordView extends FrameLayout implements TextureView.SurfaceTextureListener {

    private TextureView textureView;
    private Scene defaultScene;

    private CameraSource cameraSource;

    private PreviewOutput previewOutput;
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

        textureView = new TextureView(getContext());
        textureView.setSurfaceTextureListener(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        textureView.setLayoutParams(lp);
        addView(textureView);
    }

    public void release() {
        defaultScene.release();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        previewOutput.setSurfaceTexture(surface);
        defaultScene.load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        defaultScene.remove();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        previewOutput.setSurfaceTexture(surface);
    }

    private SceneListener sceneListener = new SceneListener() {
        @Override
        public void onSourceOpenError(Source source) {
            Toast.makeText(LalafellRecordView.this.getContext(), String.format("%s输入源打开错误", source.getName()), Toast.LENGTH_LONG).show();
        }
    };
}
