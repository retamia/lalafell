//
// Created by retamia on 2018/10/24.
//

#ifndef LIVEPLAYER_ANDROID_H
#define LIVEPLAYER_ANDROID_H

#include <jni.h>
#include <android/log.h>

#ifndef __JNILOG_TAG
#define __JNILOG_TAG (strrchr(__FILE__, '/') ? strrchr(__FILE__, '/') + 1 : __FILE__)
#endif

#define __JNILOG(LEVEL, FMT, ...) __android_log_print(LEVEL, __JNILOG_TAG, FMT, ##__VA_ARGS__)


#define LOGD(...)  __JNILOG(ANDROID_LOG_DEBUG, __VA_ARGS__)
#define LOGI(...)  __JNILOG(ANDROID_LOG_INFO, __VA_ARGS__)
#define LOGW(...)  __JNILOG(ANDROID_LOG_WARN, __VA_ARGS__)
#define LOGE(...)  __JNILOG(ANDROID_LOG_ERROR, __VA_ARGS__)

#endif //LIVEPLAYER_ANDROID_H
