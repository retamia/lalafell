//
// Created by retamia on 2018/10/18.
//

#include "live_player.h"

#include "extractor/rtmp_extractor.h"

#include "rtmp.h"

LivePlayer::LivePlayer() {
    rtmpExtractor = new RTMPExtractor();
}

LivePlayer::~LivePlayer() {
    rtmpExtractor->requestInterruption();
    rtmpExtractor->wait();
    delete rtmpExtractor;
}

void LivePlayer::prepare(const char *url) {
    this->url = std::string(url);
    rtmpExtractor->setUrl(this->url);
    rtmpExtractor->start();
}

void LivePlayer::play() {

}

void LivePlayer::release() {

}
