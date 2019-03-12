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

template<typename T>
class LinkedBlockingQueue;

class OpenGLESShader;

class GLRenderer : public RThread
{
public:
    explicit GLRenderer();
    virtual ~GLRenderer();
    void setRenderFrameQueue(LinkedBlockingQueue<RFrame *> *queue);
    void setRenderWindow(ANativeWindow *window);

protected:
    void run() override;

private:
    void initEGL();
    void drawFrame();

private:

    JNIEnv *jniEnv;
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
};


#endif //LIVEPLAYER_GL_RENDERER_H
