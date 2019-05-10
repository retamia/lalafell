//
// Created by retamia on 2018/10/18.
//

#include "lalafell_player.h"

#include "extractor/rtmp_extractor.h"
#include "decoder/h264_hw_decoder.h"
#include "renderer/gl_renderer.h"

#include "rtmp.h"
#include "__android.h"

LalaFellPlayer::LalaFellPlayer() {
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

    LOGD("init lalafell player");
}

LalaFellPlayer::~LalaFellPlayer() {
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

    LOGD("delete lalafell player");
}

void LalaFellPlayer::prepare(const char *url) {
    this->url = std::string(url);
    rtmpExtractor->setUrl(this->url);
    rtmpExtractor->start();
}

void LalaFellPlayer::play() {

}

void LalaFellPlayer::release() {
    rtmpExtractor->requestInterruption();
    rtmpExtractor->wait();

    renderer->requestInterruption();
    renderer->wait();

    videoDecodeThread->requestInterruption();
    videoDecodeThread->wait();

    LOGD("release lalafell player");
}

void LalaFellPlayer::setRendererSurface(ASurfaceTexture *surfaceTexture)
{
    LOGD("lalafell player set renderer surface: ASurfaceTexture");
    renderer->setRenderSurface(surfaceTexture);
}

void LalaFellPlayer::setRendererSurface(ANativeWindow *window)
{
    LOGD("lalafell player set renderer surface: ANativeWindow");
    renderer->setRenderSurface(window);
}
