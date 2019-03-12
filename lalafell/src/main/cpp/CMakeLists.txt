cmake_minimum_required(VERSION 3.4.1)

project(liveplayer)

set(CMAKE_INCLUDE_CURRENT_DIR ON)

add_subdirectory(libs/rtmp)
include_directories(libs/rtmp)

set(LIB_LIVE_PLAYER_HEADERS
        live_player_type_def.h
        oges/oges_shader.h
        util/thread.h
        util/linked_blocking_queue.h
        decoder/sps_decode.h
        decoder/media_codec_dequeue_thread.h
        decoder/h264_hw_decoder.h
        renderer/gl_renderer.h
        extractor/rtmp_extractor.h
        live_player.h)

set(LIB_LIVE_PLAYER_SOURCES
        util/thread.cpp
        oges/oges_shader.cpp
        decoder/media_codec_dequeue_thread.cpp
        decoder/h264_hw_decoder.cpp
        extractor/rtmp_extractor.cpp
        renderer/gl_renderer.cpp
        live_player.cpp
        live_player_jni.cpp)

set(ANDROID_LIB android log mediandk GLESv2 EGL)

add_library(${PROJECT_NAME} SHARED
        ${LIB_LIVE_PLAYER_HEADERS}
        ${LIB_LIVE_PLAYER_SOURCES})

target_link_libraries(${PROJECT_NAME}
        ${ANDROID_LIB}
        rtmp)