package org.retamia.lalafell.player;


import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;

public class LalafellPlayer {

    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;

    private int scalingMode = VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
    private long nativePlayer = 0;
    private String url;

    static {
        System.loadLibrary("liveplayer");
    }

    public LalafellPlayer() {
        this.nativePlayer = nAllocLivePlayer();
    }

    public void setDisplay(SurfaceHolder sh) {
        this.setSurface(sh.getSurface());
    }

    public void setSurface(Surface surface) {
        nPlayerSetSurface(nativePlayer, surface);
    }

    public void setVideoScalingMode(int mode) {
        scalingMode = mode;
    }

    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {

        if (path.isEmpty()) {
            throw new IllegalArgumentException("path is empty");
        }

        url = path;
    }

    public void prepare() {
        nPreparePlayer(nativePlayer, url);
    }

    public void start() throws IllegalStateException {

    }

    public void stop() throws IllegalStateException {

    }

    public void pause() throws IllegalStateException {

    }

    /*public native int getVideoWidth();

    public native int getVideoHeight();*/

    public void release() {
        nReleaseLivePlayer(nativePlayer);
    }

    private native long nAllocLivePlayer();

    private native void nReleaseLivePlayer(long pointer);

    private native void nPreparePlayer(long pointer, String url);

    private native void nPlayerSetSurface(long pointer, Surface surface);
}
