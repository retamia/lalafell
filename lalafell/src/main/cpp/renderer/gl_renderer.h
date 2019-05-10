//
// Created by retamia on 2018/10/29.
//

#ifndef LIVEPLAYER_GL_RENDERER_H
#define LIVEPLAYER_GL_RENDERER_H

#include "util/thread.h"

#include <EGL/egl.h> // requires ndk r5 or newer
#include <GLES2/gl2.h>
#include <atomic>

#include <jni.h>

struct ANativeWindow;
struct RFrame;
struct ASurfaceTexture;

template<typename T>
class LinkedBlockingQueue;

class OpenGLESShader;

class GLRenderer : public RThread
{
public:
    explicit GLRenderer();
    virtual ~GLRenderer();
    void setRenderFrameQueue(LinkedBlockingQueue<RFrame *> *queue);
    void setRenderSurface(ASurfaceTexture *surfaceTexture);
    void setRenderSurface(ANativeWindow *window);

protected:
    void run() override;

private:
    void initEGL();
    void initRenderSurface();
    void drawFrame();

private:

    ASurfaceTexture *surfaceTexture;
    ANativeWindow *window;
    LinkedBlockingQueue<RFrame *> *frameQueue;
    RFrame *lastFrame;
    EGLDisplay display;
    EGLConfig config;
    EGLint numConfigs;
    EGLint format;
    EGLSurface surface;
    EGLContext context;
    EGLint width;
    EGLint height;
    GLfloat ratio;

    OpenGLESShader *shader;

    std::atomic_bool refreshSurface;

    GLint aVertexInLocation;
    GLint aTextureInLocation;
    GLint uTextureYLocation;
    GLint uTextureULocation;
    GLint uTextureVLocation;
};


#endif //LIVEPLAYER_GL_RENDERER_H
