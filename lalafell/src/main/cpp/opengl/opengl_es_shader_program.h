//
// Created by retamia on 2018/11/13.
//

#ifndef LIVEPLAYER_OPENGL_SHADER_PROGRAM_H
#define LIVEPLAYER_OPENGL_SHADER_PROGRAM_H

enum class ShaderType {
    Vertex                 = 0x0001,
    Fragment               = 0x0002,
    Geometry               = 0x0004,
    TessellationControl    = 0x0008,
    TessellationEvaluation = 0x0010,
    Compute                = 0x0020
};

class OpenGLESShaderProgram
{
public:
    OpenGLESShaderProgram();

    bool addShaderFromSource(ShaderType type, const char *source);

    void bindAttributeLocation(const char *name, int location);
    int attributeLocation(const char *name);
    int uniformLocation(const char *name);

    void setAttributeValue();

    bool create();
    bool link();
    bool bind();

private:

};


#endif //LIVEPLAYER_OPENGL_SHADER_PROGRAM_H
