//
// Created by retamia on 2018/10/18.
//

#ifndef LIVEPLAYER_LIVE_PLAYER_H
#define LIVEPLAYER_LIVE_PLAYER_H

#include <string>

class RTMPExtractor;
class ANativeWindow;

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

};


#endif //LIVEPLAYER_LIVE_PLAYER_H
