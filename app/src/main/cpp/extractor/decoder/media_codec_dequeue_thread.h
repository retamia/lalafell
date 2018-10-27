//
// Created by retamia on 2018/10/26.
//

#ifndef LIVEPLAYER_MEDIA_CODEC_DEQUEUE_THREAD_H
#define LIVEPLAYER_MEDIA_CODEC_DEQUEUE_THREAD_H

#include "util/thread.h"

struct AMediaCodec;

class MediaCodecDequeueThread : public RThread
{
public:
    explicit MediaCodecDequeueThread();
    virtual ~MediaCodecDequeueThread();

    void setMediaCodec(AMediaCodec *mediaCodec) { this->mediaCodec = mediaCodec;}

protected:
    void run() override;

private:
    AMediaCodec *mediaCodec;
};


#endif //LIVEPLAYER_MEDIA_CODEC_DEQUEUE_THREAD_H
