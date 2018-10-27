cmake_minimum_required(VERSION 3.4.1)

project(liveplayer)

set(CMAKE_INCLUDE_CURRENT_DIR ON)

add_subdirectory(libs/rtmp)
include_directories(libs/rtmp)

set(LIB_LIVE_PLAYER_HEADERS
        live_player_type_def.h
        util/thread.h
        util/linked_blocking_queue.h
        extractor/decoder/sps_decode.h
        extractor/decoder/media_codec_dequeue_thread.h
        extractor/decoder/h264_hw_decoder.h
        extractor/rtmp_extractor.h
        live_player.h)

set(LIB_LIVE_PLAYER_SOURCES
        util/thread.cpp
        extractor/decoder/media_codec_dequeue_thread.cpp
        extractor/decoder/h264_hw_decoder.cpp
        extractor/rtmp_extractor.cpp
        live_player.cpp
        live_player_jni.cpp)

set(ANDROID_LIB android log mediandk)

add_library(${PROJECT_NAME} SHARED
        ${LIB_LIVE_PLAYER_HEADERS}
        ${LIB_LIVE_PLAYER_SOURCES})

target_link_libraries(${PROJECT_NAME}
        ${ANDROID_LIB}
        rtmp)