package org.retamia.lalafell.record.output.opengl.object;

import android.content.Context;
import android.opengl.Matrix;

import org.retamia.lalafell.R;
import org.retamia.lalafell.record.output.opengl.base.OpenGLESShaderProgram;
import org.retamia.lalafell.record.utils.NioBufferUtils;
import org.retamia.lalafell.record.utils.TextResourceReader;

import java.nio.FloatBuffer;

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
import static android.opengl.GLES20.glFinish;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;


/**
 * default yuv420p pixel format
 */
public class Image extends LObject {


    private byte[][] data;
    private int []rowStrides;
    private int width;
    private int height;
    private int originWidth;
    private int originHeight;

    public Image() {
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

    public final int getWidth() {
        return width;
    }

    public final void setWidth(int width) {
        this.width = width;
    }

    public final int getHeight() {
        return height;
    }

    public final void setHeight(int height) {
        this.height = height;
    }

    public int getOriginWidth() {
        return originWidth;
    }

    public void setOriginWidth(int originWidth) {
        this.originWidth = originWidth;
    }

    public int getOriginHeight() {
        return originHeight;
    }

    public void setOriginHeight(int originHeight) {
        this.originHeight = originHeight;
    }

    public static class Shader {

        public static int COORD_COMPONENT_COUNT = 3;
        public static int UV_COMPONENT_COUNT = 2;

        int          []textureY = {0};
        int          []textureU = {0};
        int          []textureV = {0};

        int          uTextureY;
        int          uTextureU;
        int          uTextureV;

        int          aTextureUV;
        int          aPosition;

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

            aPosition = shaderProgram.attributeLocation("a_position");
            aTextureUV = shaderProgram.attributeLocation("a_texture_uv");

            glGenTextures(1, textureY, 0);
            glBindTexture(GL_TEXTURE_2D, textureY[0]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glBindTexture(GL_TEXTURE_2D, 0);

            glGenTextures(1, textureU, 0);
            glBindTexture(GL_TEXTURE_2D, textureU[0]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glBindTexture(GL_TEXTURE_2D, 0);

            glGenTextures(1, textureV, 0);
            glBindTexture(GL_TEXTURE_2D, textureV[0]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glBindTexture(GL_TEXTURE_2D, 0);
        }

        public void setProjectionM(float []matrix) {
            this.projectionM = matrix;
        }

        public void drawImage(Image image) {

            shaderProgram.bind();

            /*float []vertexCoords = {
                    image.getPosition().getX() + image.getWidth(), image.getPosition().getY(), image.getPosition().getZ(), //top right
                    image.getPosition().getX() + image.getWidth(), image.getPosition().getY() + image.getHeight(), image.getPosition().getZ(), // bottom right
                    image.getPosition().getX(), image.getPosition().getY(), image.getPosition().getZ(), // top left
                    image.getPosition().getX(), image.getPosition().getY(), image.getPosition().getZ(), // top left
                    image.getPosition().getX() + image.getWidth(), image.getPosition().getY() + image.getHeight(), image.getPosition().getZ(), // bottom right
                    image.getPosition().getX(), image.getPosition().getY() + image.getHeight(), image.getPosition().getZ(),   // bottom left
            };*/

            float []vertexCoords = {
                    1.0f, 1.0f, image.getPosition().getZ(), //top right
                    1.0f, -1.0f, image.getPosition().getZ(), // bottom right
                    -1.0f, 1.0f, image.getPosition().getZ(), // top left
                    -1.0f, 1.0f, image.getPosition().getZ(), // top left
                    1.0f, -1.0f, image.getPosition().getZ(), // bottom right
                    -1.0f, -1.0f, image.getPosition().getZ(),   // bottom left
            };

            FloatBuffer buffer = NioBufferUtils.FromFloat(vertexCoords);
            shaderProgram.enableAttributeArray(aPosition);
            shaderProgram.setAttributeArray(aPosition, COORD_COMPONENT_COUNT, buffer, COORD_COMPONENT_COUNT * NioBufferUtils.BYTES_PER_FLOAT);

            float []uv = {
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0,0f, 0.0f,
            };
            FloatBuffer uvBuffer = NioBufferUtils.FromFloat(uv);
            shaderProgram.enableAttributeArray(aTextureUV);
            shaderProgram.setAttributeArray(aTextureUV, UV_COMPONENT_COUNT, uvBuffer, UV_COMPONENT_COUNT * NioBufferUtils.BYTES_PER_FLOAT);

            shaderProgram.setUniformValue(uProjection, 1, projectionM);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureY[0]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, image.rowStrides[0], image.getOriginHeight(), 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, NioBufferUtils.FromByte(image.data[0]));
            shaderProgram.setUniformValue(uTextureY, 0);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, textureU[0]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, image.rowStrides[1], image.getOriginHeight() / 2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, NioBufferUtils.FromByte(image.data[1]));
            shaderProgram.setUniformValue(uTextureU, 1);

            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, textureV[0]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, image.rowStrides[2], image.getOriginHeight() / 2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, NioBufferUtils.FromByte(image.data[2]));
            shaderProgram.setUniformValue(uTextureV, 2);

            glDrawArrays(GL_TRIANGLES, 0, 6);

            shaderProgram.disableAttributeArray(aPosition);
            shaderProgram.disableAttributeArray(aTextureUV);
        }
    }
}
