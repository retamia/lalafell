package org.retamia.lalafell;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.retamia.lalafell.player.LalafellPlayer;

import java.io.IOException;

public class LalafellVideoView extends FrameLayout implements TextureView.SurfaceTextureListener {

    private TextureView mRenderView = null;
    private LalafellPlayer mPlayer = null;

    public LalafellVideoView(@NonNull Context context) {
        super(context);
    }

    public LalafellVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LalafellVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LalafellVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initVideoView() {
        mPlayer = new LalafellPlayer();
    }

    public void setLiveUrl(String url, String stream) {

        initVideoView();

        try {
            mPlayer.setDataSource(url + stream);
        } catch (IOException e) {

        }
    }

    public void open() {
        if (mRenderView == null) {
            mRenderView = new TextureView(this.getContext());
            mRenderView.setSurfaceTextureListener(this);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            mRenderView.setLayoutParams(lp);
        }

        addView(mRenderView);
        mPlayer.prepare();
    }

    public void release() {

        if (mPlayer == null) {
            return;
        }

        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mPlayer.setSurface(new Surface(surfaceTexture));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        mPlayer.setSurface(new Surface(surfaceTexture));
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        mPlayer.setSurface(new Surface(surfaceTexture));
    }
}
