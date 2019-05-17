//
// Created by retamia on 2018/11/13.
//

#ifndef LIVEPLAYER_OPENGL_SHADER_PROGRAM_H
#define LIVEPLAYER_OPENGL_SHADER_PROGRAM_H

#include <EGL/egl.h> // requires ndk r5 or newer
#include <GLES2/gl2.h>
#include <vector>

enum class ShaderType {
    Vertex                 = 0x0001,
    Fragment               = 0x0002,
    //Compute                = 0x0020
};

class OpenGLESShader
{
public:
    OpenGLESShader();
    virtual ~OpenGLESShader();

    bool addShaderFromSourceCode(ShaderType type, const char *source);

    int attributeLocation(const char *name);
    int uniformLocation(const char *name);

    void setAttributeValue(int location, GLfloat value);
    void setAttributeValue(int location, GLfloat x, GLfloat y);
    void setAttributeValue(int location, GLfloat x, GLfloat y, GLfloat z);
    void setAttributeValue(int location, GLfloat x, GLfloat y, GLfloat z, GLfloat w);

    void setUniformValue(int location, GLint value);
    void setUniformValue(int location, GLfloat value);
    void setUniformValue(int location, GLfloat x, GLfloat y, GLfloat z);
    void setUniformValue(int location, GLfloat x, GLfloat y, GLfloat z, GLfloat w);

    void setAttributeArray(int location, const GLfloat *values, int tupleSize, int stride = 0);
    void setAttributeArray(int location, GLenum type, const void *values, int tupleSize, int stride = 0);

    void enableAttributeArray(int location);
    void disableAttributeArray(int location);


    bool link();
    bool bind();

private:
    bool                linked;
    std::vector<GLuint> shaders;
    GLuint              program;
};


#endif //LIVEPLAYER_OPENGL_SHADER_PROGRAM_H
