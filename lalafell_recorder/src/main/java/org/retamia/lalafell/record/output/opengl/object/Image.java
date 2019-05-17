package org.retamia.lalafell.record.output.opengl.object;

import org.retamia.lalafell.record.output.opengl.base.OpenGLESShaderProgram;

public class Image {

    private int width;
    private int height;
    private float x;
    private float y;

    OpenGLESShaderProgram shaderProgram;

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setShaderProgram(OpenGLESShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw() {
        shaderProgram.bind();
    }
}
