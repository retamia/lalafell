package org.retamia.lalafell.record;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;
import android.widget.Toast;

import org.retamia.lalafell.R;
import org.retamia.lalafell.record.output.MediaCodecOutput;
import org.retamia.lalafell.record.output.PreviewOutput;
import org.retamia.lalafell.record.scene.Scene;
import org.retamia.lalafell.record.scene.SceneListener;
import org.retamia.lalafell.record.source.Source;
import org.retamia.lalafell.record.source.CameraSource;

public class LalafellRecordActivity extends AppCompatActivity {

    public static String ACTION = "org.retamia.lalafell.record.LalafellRecordActivity";

    private GLSurfaceView glSurfaceView;
    private Scene defaultScene;

    private CameraSource cameraSource;

    private PreviewOutput previewOutput;
    private PreviewOutput.PreviewOutputRenderer previewOutputRenderer;
    private MediaCodecOutput mediaCodecOutput;

    public static void Start(Context context) {
        Intent intent = new Intent(ACTION);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_record);

        glSurfaceView = findViewById(R.id.lalafell_record_gl_preview);

        cameraSource = new CameraSource(this);
        previewOutput = new PreviewOutput(this);
        mediaCodecOutput = new MediaCodecOutput(this);

        defaultScene = new Scene(this);
        defaultScene.addSource(cameraSource);
        defaultScene.addOutput(previewOutput);

        defaultScene.setListener(sceneListener);
        defaultScene.init();

        glSurfaceView.setEGLContextClientVersion(2);
        previewOutputRenderer = new PreviewOutput.PreviewOutputRenderer(this, previewOutput);
        glSurfaceView.setRenderer(previewOutputRenderer);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        defaultScene.load();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        defaultScene.remove();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        defaultScene.release();
    }

    private SceneListener sceneListener = new SceneListener() {
        @Override
        public void onSourceOpenError(Source source) {
            Toast.makeText(LalafellRecordActivity.this, String.format("%s输入源打开错误", source.getName()), Toast.LENGTH_LONG).show();
        }
    };
}
