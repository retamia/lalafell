package org.retamia.lalafell.record.output.opengl.object;

import android.content.Context;

import org.retamia.lalafell.R;
import org.retamia.lalafell.record.output.opengl.base.OpenGLESShaderProgram;
import org.retamia.lalafell.record.utils.NioBufferUtils;
import org.retamia.lalafell.record.utils.TextResourceReader;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

public class Triangle extends LObject {

    public static final class Shader {
        private OpenGLESShaderProgram testShader;
        private Context context;

        private int aPositionLocation;
        private int uColorLocation;

        public Shader(Context context) {
            this.context = context;

            testShader = new OpenGLESShaderProgram();
            try {
                testShader.addShaderFromSourceCode(OpenGLESShaderProgram.ShaderType.Vertex,
                        TextResourceReader.readTextFileFromResource(this.context, R.raw.test_vertex));

                testShader.addShaderFromSourceCode(OpenGLESShaderProgram.ShaderType.Fragment,
                        TextResourceReader.readTextFileFromResource(this.context, R.raw.test_fragment));
            } catch (RuntimeException e) {
                throw e;
            }

            testShader.link();
            testShader.bind();

            aPositionLocation = testShader.attributeLocation("a_position");
            uColorLocation = testShader.uniformLocation("u_color");
        }

        public void draw() {
            testShader.bind();
            float []points = {
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                0.0f, 1.0f, 1.0f,
            };
            FloatBuffer buffer = NioBufferUtils.FromFloat(points);
            testShader.enableAttributeArray(aPositionLocation);
            testShader.setAttributeArray(aPositionLocation, 3, buffer, 3 * NioBufferUtils.BYTES_PER_FLOAT);
            testShader.setUniformValue(uColorLocation, 0.5f, 0.5f, 0.5f, 1.0f);

            glDrawArrays(GL_TRIANGLES, 0, 3);
        }
    }
}
