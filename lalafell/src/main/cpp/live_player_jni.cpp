
#include <jni.h>
#include <string>
#include <android/native_window_jni.h>

#include "live_player.h"

#define PLAYER(p) ((LivePlayer *)p)

extern "C" {
JNIEXPORT jlong JNICALL
Java_org_retamia_lalafell_player_LalafellPlayer_nAllocLivePlayer(JNIEnv *env, jobject thiz)
{
    LivePlayer *player = new LivePlayer();
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
Java_org_retamia_lalafell_player_LalafellPlayer_nPlayerSetSurface(JNIEnv *env, jobject thiz, jlong p, jobject surface)
{
    PLAYER(p)->setRendererSurface(ANativeWindow_fromSurface(env, surface));
}

}

