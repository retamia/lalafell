//
// Created by retamia on 2018/10/24.
//


#include "h264_hw_decoder.h"

#include <media/NdkMediaFormat.h>
#include <media/NdkMediaCodec.h>


#include "__android.h"
#include "sps_decode.h"
#include "media_codec_dequeue_thread.h"
#include "live_player_type_def.h"
#include "util/linked_blocking_queue.h"

H264HwDecoder::H264HwDecoder() : mediaCodec(nullptr), mediaFormat(nullptr), dequeueThread(nullptr)
{
}

H264HwDecoder::~H264HwDecoder()
{
    release();
}

void H264HwDecoder::setVideoFrameQueue(LinkedBlockingQueue<RFrame *> *videoFrameQueue)
{
    this->renderQueue = videoFrameQueue;
}

void H264HwDecoder::setPacketQueue(LinkedBlockingQueue<RRtmpPacket *> *queue)
{
    this->packetQueue = queue;
}

void H264HwDecoder::run()
{
    RRtmpPacket *packet = nullptr;

    while (!isInterruptionRequested()) {
        packet = packetQueue->dequeue();

        if (packet->data[0] == 0x17 && packet->data[1] == 0x00) {
            if (!decodeMetadata(packet)) {
                goto fail;
            }
        } else {
            decodeFrame(packet);
        }

        free(packet->data);
        delete packet;
        packet = nullptr;
    }

    fail:
    if (packet != nullptr) {
        free(packet->data);
        delete packet;
        packet = nullptr;
    }
}

bool H264HwDecoder::decodeMetadata(RRtmpPacket *packet)
{
    uint8_t *sps = nullptr, *pps = nullptr;
    int spsLen, ppsLen;

    extractSpsPps(packet, &sps, &spsLen, &pps, &ppsLen);

    int width = 0, height = 0, fps = 0;

    int result;
    if (sps[0] == 0x00 && sps[1] == 0x00 && sps[2] == 0x01) {
        result = h264_decode_sps(sps + 3, spsLen - 3, width, height, fps);
    } else if (sps[0] == 0x00 && sps[1] == 0x00 && sps[2] == 0x00 && sps[3] == 0x01) {
        result = h264_decode_sps(sps + 4, spsLen - 4, width, height, fps);
    } else {
        LOGE("sps start code error");
        return false;
    }

    if(!result) {
        LOGE("decode sps error");
        return false;
    }

    if (mediaFormat != nullptr) {
        AMediaFormat_delete(mediaFormat);
    }

    mediaFormat = AMediaFormat_new();
    AMediaFormat_setString(mediaFormat, AMEDIAFORMAT_KEY_MIME, "video/avc");
    AMediaFormat_setInt32(mediaFormat, AMEDIAFORMAT_KEY_WIDTH, width);
    AMediaFormat_setInt32(mediaFormat, AMEDIAFORMAT_KEY_HEIGHT, height);
    AMediaFormat_setBuffer(mediaFormat , "csd-0", sps, spsLen);
    AMediaFormat_setBuffer(mediaFormat, "csd-1", pps, ppsLen);
    AMediaFormat_setInt32(mediaFormat , AMEDIAFORMAT_KEY_COLOR_FORMAT, MEDIA_CODEC_COLOR_FMT_YUV420P);

    if (mediaCodec == nullptr) {
        mediaCodec = AMediaCodec_createDecoderByType("video/avc");

    }

    free(sps);
    free(pps);

    int configure = AMediaCodec_configure(mediaCodec, mediaFormat, NULL, NULL, 0);
    if (configure != AMEDIA_OK) {
        release();
        LOGE("mediaccodec configure error");
        return false;
    }

    if (dequeueThread == nullptr) {
        AMediaCodec_start(mediaCodec);
        dequeueThread = new MediaCodecDequeueThread();
        dequeueThread->setMediaCodec(mediaCodec);
        dequeueThread->setVideoFrameQueue(renderQueue);
        dequeueThread->start();
    }

    return true;
}

void H264HwDecoder::decodeFrame(RRtmpPacket *packet)
{
    if (mediaCodec == nullptr) {
        return;
    }

    static uint8_t startCode[4] = {0x00, 0x00, 0x00, 0x01};

    bool isKeyFrame = packet->data[0] == 0x17;
    int num = 5, startCodeLen = 4, len = 0;

    while (num < packet->size) {

        ssize_t bufId = AMediaCodec_dequeueInputBuffer(mediaCodec, 2000);

        if (bufId < 0) {
            break;
        }

        uint8_t *data = packet->data;

        len = (data[num] & 0x000000FF) << 24 | (data[num + 1] & 0x000000FF) << 16 | (data[num + 2] & 0x000000FF) << 8 | (data[num + 3] & 0x000000FF);
        num += startCodeLen;

        uint8_t *nalu = (uint8_t *)malloc((len + startCodeLen) * sizeof(uint8_t));
        memcpy(nalu, startCode, startCodeLen * sizeof(uint8_t));
        memcpy(nalu, data + num, len * sizeof(uint8_t));

        num += len;

        size_t size = 0;
        uint8_t *inputBuf = AMediaCodec_getInputBuffer(mediaCodec, bufId, &size);
        int status;
        if (inputBuf != nullptr && size >= len + startCodeLen) {
            memcpy(inputBuf, nalu, (len + startCodeLen) * sizeof(uint8_t));
            status = AMediaCodec_queueInputBuffer(mediaCodec, bufId, 0, len + startCodeLen, packet->pts, 0);

            if (status != AMEDIA_OK) {
                LOGE("queue input buffer error");
            }
        }

        free(nalu);
    }
}

void H264HwDecoder::extractSpsPps(RRtmpPacket *packet, uint8_t **outSps, int *outSpsLen, uint8_t **outPps, int *outPpsLen)
{
    uint8_t *data = packet->data;
    static uint8_t startCode[4] = {0x00, 0x00, 0x00, 0x01};
    int spsNum = data[10] & 0x1f, spsCount = 1, spsEnd = 11;

    while (spsCount <= spsNum) {
        *outSpsLen = ((data[spsEnd] & 0x000000FF) << 8) | (data[spsEnd + 1] & 0x000000FF);

        *outSps = (uint8_t *) malloc(*outSpsLen + 4);
        spsEnd += 2;
        memcpy(*outSps, startCode, 4);
        memcpy(*outSps + 4, data + spsEnd, *outSpsLen);

        spsEnd += *outSpsLen;
        spsCount++;

        *outSpsLen += 4;
    }

    int ppsNum = data[spsEnd] & 0x1f;
    int ppsEnd = spsEnd + 1, ppsCount = 1;

    while (ppsCount <= ppsNum) {
        *outPpsLen = ((data[ppsEnd] & 0x000000FF) << 8) | (data[ppsEnd + 1] & 0x000000FF);
        ppsEnd += 2;

        *outPps = (uint8_t *) malloc(*outPpsLen + 4);

        memcpy(*outPps, startCode, 4);
        memcpy(*outPps + 4, data + ppsEnd, *outPpsLen);

        ppsEnd +=  *outPpsLen;
        ppsCount++;

        *outPpsLen += 4;
    }
}

void H264HwDecoder::release()
{
    if (mediaFormat != nullptr) {
        AMediaFormat_delete(mediaFormat);
        mediaFormat = nullptr;
    }

    if (mediaCodec != nullptr) {
        AMediaCodec_stop(mediaCodec);
        AMediaCodec_delete(mediaCodec);
        mediaCodec = nullptr;
    }

    if (dequeueThread != nullptr) {
        dequeueThread->requestInterruption();
        dequeueThread->wait();

        delete dequeueThread;
        dequeueThread = nullptr;
    }
}
