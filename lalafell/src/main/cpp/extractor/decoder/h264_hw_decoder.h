//
// Created by retamia on 2018/10/24.
//

#ifndef LIVEPLAYER_H264_HW_DECODER_H
#define LIVEPLAYER_H264_HW_DECODER_H

#include "util/thread.h"

struct RFrame;
struct RRtmpPacket;
struct AMediaCodec;
struct AMediaFormat;

class MediaCodecDequeueThread;

template<typename T>
class LinkedBlockingQueue;

class H264HwDecoder : public RThread
{
public:
    explicit H264HwDecoder();
    virtual ~H264HwDecoder();

    void setVideoFrameQueue(LinkedBlockingQueue<RFrame *> *videoFrameQueue);
    void setPacketQueue(LinkedBlockingQueue<RRtmpPacket *> *queue);

protected:
    void run() override;

private:
    bool decodeMetadata(RRtmpPacket *packet);
    void decodeFrame(RRtmpPacket *packet);
    void extractSpsPps(RRtmpPacket *packet, uint8_t **outSps, int *outSpsLen, uint8_t **outPps, int *outPpsLen);

    void release();

private:
    LinkedBlockingQueue<RRtmpPacket *> *packetQueue;
    LinkedBlockingQueue<RFrame *> *renderQueue;

    MediaCodecDequeueThread *dequeueThread;

    AMediaCodec *mediaCodec;
    AMediaFormat *mediaFormat;
};


#endif //LIVEPLAYER_H264_HW_DECODER_H
