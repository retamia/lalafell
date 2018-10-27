#include <jni.h>
#include <string>

#include "live_player.h"

#define PLAYER(p) ((LivePlayer *)p)

extern "C" {
JNIEXPORT jlong JNICALL
Java_org_retamia_liveplayer_MainActivity_allocLivePlayer(JNIEnv *env, jobject thiz)
{
    LivePlayer *player = new LivePlayer();
    return (jlong)player;
}

JNIEXPORT void JNICALL
Java_org_retamia_liveplayer_MainActivity_preparePlayer(JNIEnv *env, jobject thiz, jlong p, jstring jUrl)
{
    const char *url = env->GetStringUTFChars(jUrl, 0);

    if (PLAYER(p) == nullptr)
        return;

    PLAYER(p)->prepare(url);

    env->ReleaseStringUTFChars(jUrl, url);
}


JNIEXPORT void JNICALL
Java_org_retamia_liveplayer_MainActivity_releaseLivePlayer(JNIEnv *env, jobject thiz, jlong p)
{
    PLAYER(p)->release();
    delete PLAYER(p);
}

}