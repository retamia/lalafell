//
// Created by retamia on 2018/10/26.
//

#include "media_codec_dequeue_thread.h"

#include "live_player_type_def.h"
#include "util/linked_blocking_queue.h"
#include <media/NdkMediaCodec.h>

#define MEDIA_CODEC_OUTPUT_TIMEOUT_US 2000

MediaCodecDequeueThread::MediaCodecDequeueThread()
{

}

MediaCodecDequeueThread::~MediaCodecDequeueThread()
{

}

void MediaCodecDequeueThread::setVideoFrameQueue(LinkedBlockingQueue<RFrame *> *videoFrameQueue)
{
    this->videoFrameQueue = videoFrameQueue;
}

void MediaCodecDequeueThread::run()
{
    while (!isInterruptionRequested()) {

        AMediaCodecBufferInfo info;

        ssize_t outBufId = AMediaCodec_dequeueOutputBuffer(mediaCodec, &info, MEDIA_CODEC_OUTPUT_TIMEOUT_US);

        if (outBufId < 0) {
            // format AMEDIACODEC_INFO_OUTPUT_FORMAT_CHANGED
            continue;
        }

        size_t size = 0;
        uint8_t *buffer = AMediaCodec_getOutputBuffer(mediaCodec, outBufId, &size);
        if (!size) {
            return;
        }

        int colorFmt;
        AMediaFormat *format = AMediaCodec_getOutputFormat(mediaCodec);
        AMediaFormat_getInt32(format, AMEDIAFORMAT_KEY_COLOR_FORMAT, &colorFmt);

        switch (colorFmt) {
            case MEDIA_CODEC_COLOR_FMT_YUV420P: {
                RFrame *frame = new RFrame;
                AMediaFormat_getInt32(format, AMEDIAFORMAT_KEY_WIDTH, &frame->videoFormat.width);
                AMediaFormat_getInt32(format, AMEDIAFORMAT_KEY_HEIGHT, &frame->videoFormat.height);
                frame->videoFormat.type = RPixelFormatType::YUV420P;
                frame->pts = info.presentationTimeUs;
                frame->linesize[0] = frame->videoFormat.width;
                frame->linesize[1] = frame->videoFormat.width / 4;
                frame->linesize[2] = frame->videoFormat.width / 4;
                size_t ySize = frame->linesize[0] * frame->videoFormat.height;
                size_t uvSize = frame->linesize[1] * frame->videoFormat.height;
                frame->data[0] = (uint8_t *)malloc((ySize + uvSize) * sizeof(uint8_t));
                frame->data[1] = frame->data[0] + ySize;
                frame->data[2] = frame->data[1] + ySize + uvSize;

                memcpy(frame->data[0], buffer, ySize);
                memcpy(frame->data[1], buffer + ySize, uvSize);
                memcpy(frame->data[2], buffer + ySize + uvSize, uvSize);

                this->videoFrameQueue->enqueue(frame);
            };
            default: {

            }
        }

        AMediaCodec_releaseOutputBuffer(mediaCodec, outBufId, false);
    }
}
