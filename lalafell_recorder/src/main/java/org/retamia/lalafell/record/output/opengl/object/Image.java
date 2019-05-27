package org.retamia.lalafell.record.output.opengl.object;

import android.content.Context;
import android.opengl.GLUtils;

import org.retamia.lalafell.R;
import org.retamia.lalafell.record.output.opengl.base.OpenGLESShaderProgram;
import org.retamia.lalafell.record.utils.ByteBufferUtils;
import org.retamia.lalafell.record.utils.TextResourceReader;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LUMINANCE;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE2;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;


/**
 * default yuv420p pixel format
 */
public class Image {

    private int width;
    private int height;
    private float x;
    private float y;
    private byte[][] data;
    private int []rowStrides;

    public Image() {
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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public byte[][] getData() {
        return data;
    }

    public void setData(byte[][] data) {
        this.data = data;
    }

    public int[] getRowStrides() {
        return rowStrides;
    }

    public void setRowStrides(int[] rowStrides) {
        this.rowStrides = rowStrides;
    }

    public static class Shader {

        int          []textureY = {0};
        int          []textureU = {0};
        int          []textureV = {0};

        int          uTextureY;
        int          uTextureU;
        int          uTextureV;

        int          aTextureUV;
        int          aVertex;

        int          uProjection;

        float        []projectionM;

        OpenGLESShaderProgram shaderProgram;
        private Context context;

        public Shader(Context context) {

            this.context = context;

            shaderProgram = new OpenGLESShaderProgram();

            try {
                shaderProgram.addShaderFromSourceCode(OpenGLESShaderProgram.ShaderType.Vertex,
                        TextResourceReader.readTextFileFromResource(this.context, R.raw.image_vertex));

                shaderProgram.addShaderFromSourceCode(OpenGLESShaderProgram.ShaderType.Fragment,
                        TextResourceReader.readTextFileFromResource(this.context, R.raw.image_fragment));
            } catch (RuntimeException e) {
                throw e;
            }

            shaderProgram.link();
            shaderProgram.bind();

            uProjection = shaderProgram.uniformLocation("u_projection");

            uTextureY = shaderProgram.uniformLocation("u_texture_y");
            uTextureU = shaderProgram.uniformLocation("u_texture_u");
            uTextureV = shaderProgram.uniformLocation("u_texture_v");

            aVertex = shaderProgram.attributeLocation("a_vertex");
            aTextureUV = shaderProgram.attributeLocation("a_texture_uv");

            glGenTextures(1, textureY, 0);
            glBindTexture(GL_TEXTURE_2D, textureY[0]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glBind

            glGenTextures(1, textureU, 0);
            glBindTexture(GL_TEXTURE_2D, textureU[0]);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            glGenTextures(1, textureV, 0);
            glBindTexture(GL_TEXTURE_2D, textureV[0]);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        public void setProjectionM(float []matrix) {
            this.projectionM = matrix;
        }

        public void drawImage(Image image) {

            shaderProgram.bind();

            float []points = {
                    0, 0, 0.0f, 0.0f,
                    image.width, 0, 1.0f, 0.0f,
                    0, image.height, 0.0f, 1.0f,
                    image.width, 0, 1.0f, 0.0f,
                    0, image.height, 0.0f, 1.0f,
                    image.width, image.height, 1.0f, 1.0f
            };

            shaderProgram.setAttributeArray(aVertex, 2, points, 2 * ByteBufferUtils.BYTES_PER_FLOAT);
            shaderProgram.setAttributeArray(aTextureUV, 2, points, 2 * ByteBufferUtils.BYTES_PER_FLOAT, 2);

            shaderProgram.setUniformValue(uProjection, 1, projectionM);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureY[0]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, image.rowStrides[0], image.height, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, ByteBufferUtils.FromByte(image.data[0]));
            shaderProgram.setUniformValue(uTextureY, 0);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, textureU[0]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, image.rowStrides[1], image.height / 4, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, ByteBufferUtils.FromByte(image.data[1]));
            shaderProgram.setUniformValue(uTextureU, 1);

            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, textureV[0]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, image.rowStrides[2], image.height / 4, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, ByteBufferUtils.FromByte(image.data[2]));
            shaderProgram.setUniformValue(uTextureV, 2);

            shaderProgram.enableAttributeArray(aVertex);
            shaderProgram.enableAttributeArray(aTextureUV);

            glDrawArrays(GL_TRIANGLES, 0, 3);
            glDrawArrays(GL_TRIANGLES, 3, 6);

            shaderProgram.disableAttributeArray(aVertex);
            shaderProgram.disableAttributeArray(aTextureUV);
        }
    }
}
