package org.retamia.lalafell.record.output.opengl.base;

import android.support.annotation.IdRes;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_INVALID_VALUE;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

public final class OpenGLESShaderProgram {

    public static final int BYTES_PER_FLOAT = 4;

    private static final String TAG = OpenGLESShaderProgram.class.getName();

    private boolean linked = false;
    List<Integer>   shaders = new ArrayList<>();
    int             program = -1;

    enum ShaderType {
        Vertex,
        Fragment,
    }

    public boolean addShaderFromSourceCode(ShaderType type, String source) {
        int shader;
        if (type == ShaderType.Vertex) {
            shader = glCreateShader(GL_VERTEX_SHADER);
        } else if (type == ShaderType.Fragment) {
            shader = glCreateShader(GL_FRAGMENT_SHADER);
        } else {
            return false;
        }

        glShaderSource(shader, source);
        glCompileShader(shader);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {

            glDeleteShader(shader);
            Log.e(TAG, "compile shader error");
            return false;
        }

        shaders.add(shader);
        return true;
    }

    public int attributeLocation(String name) {
        return glGetAttribLocation(program, name);
    }

    public int uniformLocation(String name) {
        return glGetUniformLocation(program, name);
    }

    public void setAttributeValue(int location, float value) {
        final FloatBuffer buffer = ByteBuffer
                .allocateDirect(BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(value);

        glVertexAttribPointer(location, 1, GL_FLOAT, false, 0, buffer);
    }

    public void setAttributeValue(int location, float x, float y) {
        final float []value = {x, y};
        final FloatBuffer buffer = ByteBuffer
                .allocateDirect(BYTES_PER_FLOAT * 2)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(value);

        glVertexAttribPointer(location, 2, GL_FLOAT, false, 0, buffer);
    }

    public void setAttributeValue(int location, float x, float y, float z) {
        final float []value = {x, y, z};
        final FloatBuffer buffer = ByteBuffer
                .allocateDirect(BYTES_PER_FLOAT * 3)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(value);

        glVertexAttribPointer(location, 3, GL_FLOAT, false, 0, buffer);
    }

    public void setAttributeValue(int location, float x, float y, float z, float w) {
        final float []value = {x, y, z , w};
        final FloatBuffer buffer = ByteBuffer
                .allocateDirect(BYTES_PER_FLOAT * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(value);

        glVertexAttribPointer(location, 4, GL_FLOAT, false, 0, buffer);
    }

    public void setUniformValue(int location, int value) {
        glUniform1i(location, value);
    }

    public void setUniformValue(int location, float value) {
        glUniform1f(location, value);
    }

    public void setUniformValue(int location, float x, float y, float z) {
        glUniform3f(location, x, y, z);
    }

    public void setUniformValue(int location, float x, float y, float z, float w) {
        glUniform4f(location, x, y, z, w);
    }

    public void setAttributeArray(int location, final float []values) {
        setAttributeArray(location, values, 0);
    }

    public void setAttributeArray(int location, final float []values, int stride) {

        final FloatBuffer buffer = ByteBuffer
                .allocateDirect(BYTES_PER_FLOAT * values.length)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(values);

        glVertexAttribPointer(location, values.length, GL_FLOAT, false, stride, buffer);
    }

    public void setAttributeArray(int location, int type, final float []values) {
        setAttributeArray(location, type, values, 0);
    }

    public void setAttributeArray(int location, int type, final float []values, int stride) {
        final FloatBuffer buffer = ByteBuffer
                .allocateDirect(BYTES_PER_FLOAT * values.length)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(values);

        glVertexAttribPointer(location, values.length, type, false, stride, buffer);
    }

    public void enableAttributeArray(int location) {
        glEnableVertexAttribArray(location);
    }

    public void disableAttributeArray(int location) {
        glDisableVertexAttribArray(location);
    }

    public boolean link() {
        program = glCreateProgram();

        for (int shader: shaders) {
            glAttachShader(program, shader);
        }

        glLinkProgram(program);
        final int[] linkStatus = new int[1];
        glGetShaderiv(program, GL_LINK_STATUS, linkStatus, 0);

        for (int shader: shaders) {
            glDeleteShader(shader);
        }

        shaders.clear();

        if (linkStatus[0] == 0) {
            Log.e(TAG,"link error");
            return false;
        }

        linked = true;
        return true;
    }

    public boolean bind() {
        boolean success = true;
        if (!linked) {
            success = link();
        }

        if (!success) {
            return false;
        }

        glUseProgram(program);

        return true;
    }
}
