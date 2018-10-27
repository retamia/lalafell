//
// Created by retamia on 2018/10/23.
//

#ifndef LIVEPLAYER_RTMP_EXTRACTOR_H
#define LIVEPLAYER_RTMP_EXTRACTOR_H

#include <string>

#include "util/thread.h"
#include "util/linked_blocking_queue.h"

class H264HwDecoder;
struct RRtmpPacket;
struct RTMP;

class RTMPExtractor : public RThread{
public:
    explicit RTMPExtractor();
    virtual ~RTMPExtractor();

    void setUrl(const std::string &url);

protected:
    void run() override;

private:
    bool openRTMP();

private:
    RTMP *rtmp;
    std::string url;
    LinkedBlockingQueue<RRtmpPacket *> videoPacketQueue;
    LinkedBlockingQueue<RRtmpPacket *> audioPacketQueue;
    H264HwDecoder *decodeThread;
};


#endif //LIVEPLAYER_RTMP_EXTRACTOR_H
