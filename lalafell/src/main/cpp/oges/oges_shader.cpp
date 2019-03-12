//
// Created by retamia on 2018/11/13.
//

#include "oges_shader.h"

#include "__android.h"

OpenGLESShader::OpenGLESShader() : linked(false)
{

}

bool OpenGLESShader::addShaderFromSourceCode(ShaderType type, const char *source)
{
    GLuint shader;
    int success = -1;
    if (type == ShaderType::Vertex) {
        shader = glCreateShader(GL_VERTEX_SHADER);
    } else if (type == ShaderType::Fragment) {
        shader = glCreateShader(GL_FRAGMENT_SHADER);
    } else {
        return false;
    }

    glCompileShader(shader);
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);

    if (!success) {
        LOGE("compile gl error");
        return false;
    }

    shaders.push_back(shader);
    return true;
}

int OpenGLESShader::attributeLocation(const char *name)
{
    return glGetAttribLocation(program, name);
}

int OpenGLESShader::uniformLocation(const char *name)
{
    return glGetUniformLocation(program, name);
}

void OpenGLESShader::setAttributeValue(int location, GLfloat value)
{
    GLfloat buffer[] = {value};
    glVertexAttribPointer(location, 1, GL_FLOAT, GL_FALSE, 0, &buffer);
    glEnableVertexAttribArray(location);
}

void OpenGLESShader::setAttributeValue(int location, GLfloat x, GLfloat y)
{
    GLfloat buffer[] = {x, y};
    glVertexAttribPointer(location, 2, GL_FLOAT, GL_FALSE, 0, &buffer);
    glEnableVertexAttribArray(location);
}

void OpenGLESShader::setAttributeValue(int location, GLfloat x, GLfloat y, GLfloat z)
{
    GLfloat buffer[] = {x, y, z};
    glVertexAttribPointer(location, 3, GL_FLOAT, GL_FALSE, 0, &buffer);
    glEnableVertexAttribArray(location);
}

void OpenGLESShader::setAttributeValue(int location, GLfloat x, GLfloat y, GLfloat z, GLfloat w)
{
    GLfloat buffer[] = {x, y, z, w};
    glVertexAttribPointer(location, 4, GL_FLOAT, GL_FALSE, 0, &buffer);
    glEnableVertexAttribArray(location);
}

void OpenGLESShader::setUniformValue(int location, GLint value)
{
    glUniform1i(location, value);
}

void OpenGLESShader::setUniformValue(int location, GLfloat value)
{
    glUniform1f(location, value);
}

bool OpenGLESShader::link()
{
    program = glCreateProgram();
    int success;

    for (GLuint shader: shaders) {
        glAttachShader(program, shader);
    }

    glLinkProgram(program);
    glGetProgramiv(program, GL_LINK_STATUS, &success);

    for (GLuint shader: shaders) {
        glDeleteShader(shader);
    }

    shaders.clear();

    if (!success) {
        LOGE("link error");
        return false;
    }

    linked = true;
    return true;
}

bool OpenGLESShader::bind()
{
    bool success = true;
    if (!linked) {
        success = link();
    }

    if (!success) {
        return false;
    }

    glUseProgram(program);
    return true;
}
