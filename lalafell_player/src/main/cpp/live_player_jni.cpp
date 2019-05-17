
#include <jni.h>
#include <string>

#include <android/surface_texture_jni.h>
#include <android/native_window_jni.h>

#include <android/log.h>
#include "lalafell_player.h"

#define PLAYER(p) ((LalaFellPlayer *) p)

extern "C" {
JNIEXPORT jlong JNICALL
Java_org_retamia_lalafell_player_LalafellPlayer_nAllocLivePlayer(JNIEnv *env, jobject thiz)
{
    LalaFellPlayer *player = new LalaFellPlayer();
    return (jlong)player;
}

JNIEXPORT void JNICALL
Java_org_retamia_lalafell_player_LalafellPlayer_nPreparePlayer(JNIEnv *env, jobject thiz, jlong p, jstring jUrl)
{
    const char *url = env->GetStringUTFChars(jUrl, 0);

    if (PLAYER(p) == nullptr)
        return;

    PLAYER(p)->prepare(url);

    env->ReleaseStringUTFChars(jUrl, url);
}


JNIEXPORT void JNICALL
Java_org_retamia_lalafell_player_LalafellPlayer_nReleaseLivePlayer(JNIEnv *env, jobject thiz, jlong p)
{
    PLAYER(p)->release();
    delete PLAYER(p);
}

JNIEXPORT void JNICALL
Java_org_retamia_lalafell_player_LalafellPlayer_nPlayerSetSurfaceTexture(JNIEnv *env, jobject thiz, jlong p, jobject surfaceTexture)
{
#if __ANDROID_API__ >= 28
    PLAYER(p)->setRendererSurface(ASurfaceTexture_fromSurfaceTexture(env, surfaceTexture));
#else
    __android_log_print(ANDROID_LOG_WARN, "lalafell jni", "android api version must greater than 28");
#endif
}

JNIEXPORT void JNICALL
Java_org_retamia_lalafell_player_LalafellPlayer_nPlayerSetSurface(JNIEnv *env, jobject thiz, jlong p, jobject surface)
{
    PLAYER(p)->setRendererSurface(ANativeWindow_fromSurface(env, surface));
}

}

