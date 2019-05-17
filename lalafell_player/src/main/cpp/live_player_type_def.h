//
// Created by retamia on 2018/10/25.
//

#ifndef LIVEPLAYER_LIVE_PLAYER_TYPE_DEF_H
#define LIVEPLAYER_LIVE_PLAYER_TYPE_DEF_H

#include <cstdint>

#define MEDIA_CODEC_COLOR_FMT_YUV420P     19
#define MEDIA_CODEC_CODEC_COLOR_FMT_NV21  21

enum class RPacketType {
    H264_PACKET,
    AAC_PACKET,
};

enum class RPixelFormatType {
    NONE = -1,
    YUV420P,
    NV12,
    RGBA,
    RGB24
};

enum class RSampleFormatType {
    NONE = -1,
    U8,         // unsigned 8 bit
    S16,        // signed 16 bit
    S32,
    S64,
    FLOAT,
    DOUBLE,

    U8_PLANAR,
    S16_PLANAR,
    S32_PLANAR,
    S64_PLANAR,
    FLOAT_PLANAR,
    DOUBLE_PLANAR,
};

struct RAudioFormat {
    RSampleFormatType type = RSampleFormatType::NONE;
    int samples = 0;
    int sampleRate = 0;
    int channels = 0;
};

struct RVideoFormat {
    RPixelFormatType type = RPixelFormatType::NONE;
    int width = 0;
    int height = 0;
};

struct RFrame {
#define PLANE_NUM 8
    uint8_t *data[PLANE_NUM];
    int linesize[PLANE_NUM];
    int64_t pts;
    bool isKeyFrame = false;
    RVideoFormat videoFormat;
    RAudioFormat audioFormat;
};

struct RRtmpPacket {
    RPacketType type;
    uint8_t *data;
    size_t   size;
    int64_t  pts;
};

#endif //LIVEPLAYER_LIVE_PLAYER_TYPE_DEF_H
