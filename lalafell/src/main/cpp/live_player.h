//
// Created by retamia on 2018/10/18.
//

#ifndef LIVEPLAYER_LIVE_PLAYER_H
#define LIVEPLAYER_LIVE_PLAYER_H

#include <string>

#include "util/linked_blocking_queue.h"

class RTMPExtractor;
class ANativeWindow;
class H264HwDecoder;
class GLRenderer;
class RRtmpPacket;
class RFrame;

class LivePlayer {
public:
    explicit LivePlayer();
    virtual ~LivePlayer();

    void prepare(const char *url);
    void play();
    void release();
    void setRendererSurface(ANativeWindow *window);


private:
    RTMPExtractor *rtmpExtractor;
    std::string url;

    H264HwDecoder *videoDecodeThread;
    GLRenderer    *renderer;


    LinkedBlockingQueue<RRtmpPacket *> videoPacketQueue;
    LinkedBlockingQueue<RRtmpPacket *> audioPacketQueue;

    LinkedBlockingQueue<RFrame *>      videoRenderQueue;
    LinkedBlockingQueue<RFrame *>      audioRenderQueue;
};


#endif //LIVEPLAYER_LIVE_PLAYER_H
