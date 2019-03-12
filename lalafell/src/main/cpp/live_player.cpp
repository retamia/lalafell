//
// Created by retamia on 2018/10/18.
//

#include "live_player.h"

#include "extractor/rtmp_extractor.h"
#include "decoder/h264_hw_decoder.h"
#include "renderer/gl_renderer.h"

#include "rtmp.h"

LivePlayer::LivePlayer() {
    rtmpExtractor = new RTMPExtractor();
    rtmpExtractor->setVideoPacketQueue(&videoPacketQueue);
    rtmpExtractor->setAudioPacketQueue(&audioPacketQueue);

    videoDecodeThread = new H264HwDecoder();
    videoDecodeThread->setPacketQueue(&videoPacketQueue);
    videoDecodeThread->setFrameQueue(&videoRenderQueue);
    videoDecodeThread->start();

    renderer = new GLRenderer();
    renderer->setRenderFrameQueue(&videoRenderQueue);
    renderer->start();

    //@TODO audio decode
    //@TODO audio output
}

LivePlayer::~LivePlayer() {
    rtmpExtractor->requestInterruption();
    rtmpExtractor->wait();
    delete rtmpExtractor;

    renderer->requestInterruption();
    renderer->wait();
    delete renderer;

    videoDecodeThread->requestInterruption();
    videoDecodeThread->wait();
    videoDecodeThread->release();
    delete videoDecodeThread;
}

void LivePlayer::prepare(const char *url) {
    this->url = std::string(url);
    rtmpExtractor->setUrl(this->url);
    rtmpExtractor->start();
}

void LivePlayer::play() {

}

void LivePlayer::release() {
    rtmpExtractor->requestInterruption();
    rtmpExtractor->wait();

    renderer->requestInterruption();
    renderer->wait();

    videoDecodeThread->requestInterruption();
    videoDecodeThread->wait();
}

void LivePlayer::setRendererSurface(ANativeWindow *window)
{
    renderer->setRenderWindow(window);
}
