//
// Created by retamia on 2018/10/29.
//

#include "gl_renderer.h"

#include <android/native_window.h>
#include <android/surface_texture.h>
#include <android/surface_texture_jni.h>

#include "__android.h"
#include "util/linked_blocking_queue.h"
#include "oges/oges_shader.h"
#include "live_player_type_def.h"

#define EGL_NO_CONFIG  EGL_CAST(EGLConfig, 0)

GLfloat vertexPoints[] = {

    -1.0f, -1.0f,  0.0f, 0.0f,  // first triangle
    1.0f, -1.0f,  1.0f, 0.0f,
    -1.0f,  1.0f,  0.0f, 1.0f,

    1.0f, -1.0f,  1.0f, 0.0f,  // second triangle
    -1.0f,  1.0f, 0.0f, 1.0f,
    1.0f,  1.0f,  1.0f, 1.0f,

};

const char *vertex_shader_source =
    "attribute vec4 a_VertexIn;\n"
    /*"attribute vec2 textureIn;\n"
    "varying vec2 textureOut;\n"*/
    "void main()\n"
    "{\n"
    "    gl_Position = a_VertexIn;\n"
    /*"    textureOut = textureIn;\n"*/
    "}";

const char *fragmeng_shader_source =
    "precision mediump float;\n"
/*    "varying vec2 textureOut;\n"
    "uniform sampler2D tex_y;\n"
    "uniform sampler2D tex_u;\n"
    "uniform sampler2D tex_v;\n"*/
    "void main()\n"
    "{\n"
    /*"    vec3 yuv;\n"
    "    vec3 rgb;\n"
    "    yuv.x = texture2D(tex_y, textureOut).r;\n"
    "    yuv.y = texture2D(tex_u, textureOut).r - 0.5;\n"
    "    yuv.z = texture2D(tex_v, textureOut).r - 0.5;\n"
    "    rgb = mat3( 1,       1,         1,\n"
    "               0,       -0.21482,  2.12798,\n"
    "               1.28033, -0.38059,  0) * yuv;\n"*/
    //"    gl_FragColor = vec4(rgb, 1);\n"
    "    gl_FragColor = vec4(1, 0, 0, 1);\n"
    "}";

GLRenderer::GLRenderer()
    : window(nullptr)
    , frameQueue(nullptr)
    , lastFrame(nullptr)
    , display(EGL_NO_DISPLAY)
    , context(EGL_NO_CONTEXT)
    , surface(EGL_NO_SURFACE)
    , config(EGL_NO_CONFIG)
    , refreshSurface(false)
{
    shader = new OpenGLESShader();
}

GLRenderer::~GLRenderer()
{
    delete shader;
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

    shader->addShaderFromSourceCode(ShaderType::Vertex, vertex_shader_source);
    shader->addShaderFromSourceCode(ShaderType::Fragment, fragmeng_shader_source);

    if (!shader->link()) {
        LOGE("shader link error");
        return;
    }

    aTextureInLocation = shader->attributeLocation("a_VertexIn");
    LOGI("initEGL success");
}

void GLRenderer::setRenderSurface(ASurfaceTexture *surfaceTexture)
{
    refreshSurface = false;
    this->surfaceTexture = surfaceTexture;

#if __ANDROID_API__ >= 28
    this->window = ASurfaceTexture_acquireANativeWindow(surfaceTexture);
    initRenderSurface();
#endif

    refreshSurface = true;
}

void GLRenderer::setRenderSurface(ANativeWindow *window)
{
    refreshSurface = false;
    this->window = window;
    initRenderSurface();
    refreshSurface = true;
}

void GLRenderer::initRenderSurface()
{
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

    glViewport(0, 0, width, height);

    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
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
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    shader->setAttributeArray(aVertexInLocation, vertexPoints, 2, sizeof(GLfloat) * 4);
    shader->enableAttributeArray(aVertexInLocation);

    glDrawArrays(GL_TRIANGLES, 0, 3);
    glDrawArrays(GL_TRIANGLES, 3, 6);
    //shader->setAttributeArray(aTextureInLocation, vertexPoints + (2 * sizeof(GLfloat)), 2, sizeof(GLfloat) * 4);
}