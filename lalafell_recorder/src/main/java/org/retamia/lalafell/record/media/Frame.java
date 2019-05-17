package org.retamia.lalafell.record.media;


import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class Frame {

    public enum PixelFmt {
        YUV420P,
        NV12,
        RGBA8888,
        ARGB8888,
    }

    private byte[][] data;
    private PixelFmt  format;
    private int   width;
    private int   height;
    private int   plane;
    private int[] rowStride;
    private long  presentationNanoTime;

    public byte[][] getData() {
        return data;
    }

    public void setData(byte[][] data) {
        this.data = data;
    }

    public int getPlane() {
        return plane;
    }

    public void setPlane(int plane) {
        this.plane = plane;
    }

    public int[] getRowStride() {
        return rowStride;
    }

    public void setRowStride(int[] rowStride) {
        this.rowStride = rowStride;
    }

    public PixelFmt getFormat() {
        return format;
    }

    public void setFormat(PixelFmt format) {
        this.format = format;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getPresentationNanoTime() {
        return presentationNanoTime;
    }

    public void setPresentationNanoTime(long presentationNanoTime) {
        this.presentationNanoTime = presentationNanoTime;
    }
}
