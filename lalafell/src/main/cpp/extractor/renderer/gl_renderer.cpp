//
// Created by retamia on 2018/10/29.
//

#include "gl_renderer.h"

#include <android/native_window.h>

#include "__android.h"
#include "util/linked_blocking_queue.h"
#include "live_player_type_def.h"

#define EGL_NO_CONFIG  EGL_CAST(EGLConfig,0)

GLRenderer::GLRenderer()
    : window(nullptr)
    , frameQueue(nullptr)
    , lastFrame(nullptr)
    , display(EGL_DEFAULT_DISPLAY)
    , context(EGL_NO_CONTEXT)
    , surface(EGL_NO_SURFACE)
    , config(EGL_NO_CONFIG)
    , refreshSurface(false)
{

}

GLRenderer::~GLRenderer()
{

}

void GLRenderer::setRenderFrameQueue(LinkedBlockingQueue<RFrame *> *queue)
{
    this->frameQueue = queue;
}

void GLRenderer::initEGL()
{
    const EGLint attribs[] = {
        EGL_SURFACE_TYPE, EGL_WINDOW_BIT, EGL_RENDERABLE_TYPE,
        EGL_OPENGL_ES2_BIT, EGL_BLUE_SIZE, 8, EGL_GREEN_SIZE, 8, EGL_RED_SIZE,
        8, EGL_ALPHA_SIZE, 8, EGL_DEPTH_SIZE, 0, EGL_STENCIL_SIZE, 0,
        EGL_NONE};

    if ((display = eglGetDisplay(EGL_DEFAULT_DISPLAY)) == EGL_NO_DISPLAY) {
        LOGE("eglGetDisplay() returned error %d", eglGetError());
        return;
    }

    EGLint majorVersion, minorVersion;
    if (!eglInitialize(display, &majorVersion, &minorVersion)) {
        LOGE("eglInitialize() returned error %d", eglGetError());
        return;
    }

    if (!eglChooseConfig(display, attribs, &config, 1, &numConfigs)) {
        LOGE("eglChooseConfig() returned error %d", eglGetError());
        return;
    }

    if (!eglGetConfigAttrib(display, config, EGL_NATIVE_VISUAL_ID, &format)) {
        LOGE("eglGetConfigAttrib() returned error %d", eglGetError());
        return;
    }

    ANativeWindow_setBuffersGeometry(this->window, 0, 0, format);

    if (!(surface = eglCreateWindowSurface(display, config, this->window, nullptr))) {
        LOGE("eglCreateWindowSurface() returned error %d", eglGetError());
        return;
    }

    EGLint contextAttrs[] = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE};
    if (!(context = eglCreateContext(display, config, 0, contextAttrs))) {
        LOGE("eglCreateContext() returned error %d", eglGetError());
        return;
    }

    if (!eglMakeCurrent(display, surface, surface, context)) {
        LOGE("eglMakeCurrent() returned error %d", eglGetError());
        return;
    }

    if (!eglQuerySurface(display, surface, EGL_WIDTH, &width) ||
        !eglQuerySurface(display, surface, EGL_HEIGHT, &height)) {
        LOGE("eglQuerySurface() returned error %d", eglGetError());
        return;
    }
}

void GLRenderer::setRenderWindow(ANativeWindow *window)
{
    refreshSurface = false;

    this->window = window;

    if (display == EGL_NO_DISPLAY) {
        initEGL();
    } else {
        glClear(GL_COLOR_BUFFER_BIT);
        eglSwapBuffers(display, surface);
        eglDestroySurface(display, surface);

        surface = eglCreateWindowSurface(display, config, this->window, nullptr);

        eglMakeCurrent(display, surface, surface, context);

        eglQuerySurface(display, surface, EGL_WIDTH, &width);
        eglQuerySurface(display, surface, EGL_HEIGHT, &height);
    }

    glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    refreshSurface = true;
}

void GLRenderer::run()
{
    while (!isInterruptionRequested()) {

        RFrame *frame = frameQueue->dequeue(1000 / 25);

        if (frame == nullptr) {
            frame = lastFrame;
        }

        if (refreshSurface) {
            drawFrame();
        }

        RFrame *oldFrame = lastFrame;
        lastFrame = frame;

        if (lastFrame != oldFrame && oldFrame != nullptr) {
            free(oldFrame->data[0]);
            delete oldFrame;
        }

    }
}

void GLRenderer::drawFrame()
{
    ANativeWindow_acquire(window);
    ANativeWindow_Buffer buffer;
    if (ANativeWindow_lock(window, &buffer, nullptr) < 0) {
        LOGE("ANativeWindow_lock failed");
        return;
    }

    if (ANativeWindow_unlockAndPost(window)) {
        LOGE("ANativeWindow_unlockAndPost failed");
    }
}
