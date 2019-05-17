//
// Created by retamia on 2018/10/23.
//



#include <libs/rtmp/log.h>
#include "rtmp_extractor.h"


#include "__android.h"
#include "live_player_type_def.h"
#include "rtmp.h"

static void rtmp_log(int level, const char *fmt, va_list args)
{

}

RTMPExtractor::RTMPExtractor()
{
    rtmp = RTMP_Alloc();
}

RTMPExtractor::~RTMPExtractor()
{
    RTMP_Free(rtmp);
}

void RTMPExtractor::setUrl(const std::string &url)
{
    this->url = url;
}

void RTMPExtractor::run()
{

    openRTMP();

    RTMPPacket rtmpPacket;
    RTMPPacket_Alloc(&rtmpPacket, 4096);

    while (!isInterruptionRequested()) {

        if (!RTMP_IsConnected(rtmp)) {
            msleep(1000);
            continue;
        }

        if (!RTMP_ReadPacket(rtmp, &rtmpPacket)) {
            LOGW("packet read error");
            continue;
        }

        if (!RTMPPacket_IsReady(&rtmpPacket)) {
            continue;
        }

        if (!RTMP_ClientPacket(rtmp, &rtmpPacket)) {
            RTMPPacket_Free(&rtmpPacket);
            continue;
        }

        if (rtmpPacket.m_body == nullptr) {
            RTMPPacket_Free(&rtmpPacket);
            continue;
        }

        if (/*rtmpPacket.m_packetType != RTMP_PACKET_TYPE_AUDIO &&*/ rtmpPacket.m_packetType
            != RTMP_PACKET_TYPE_VIDEO) {
            RTMPPacket_Free(&rtmpPacket);
            continue;
        }

        RRtmpPacket *packet = new RRtmpPacket();
        packet->data = (uint8_t *) malloc(rtmpPacket.m_nBodySize * sizeof(uint8_t));
        packet->size = rtmpPacket.m_nBodySize;
        memcpy(packet->data, rtmpPacket.m_body, packet->size);
        packet->type = RPacketType::H264_PACKET;
        packet->pts = static_cast<int64_t>(rtmpPacket.m_nTimeStamp) * 1000;
        videoPacketQueue->enqueue(packet);

        /*if (rtmpPacket.m_packetType == RTMP_PACKET_TYPE_VIDEO) {
            packet->type = RPacketType::VIDEO;
            videoPacketQueue.enqueue(packet);
        } else {
            packet->size = rtmpPacket.m_nBodySize;
            audioPacketQueue.enqueue(packet);
        }*/

        RTMPPacket_Free(&rtmpPacket);
    }

    LOGD("rtmp extractor finished");
}

// @TODO swf
bool RTMPExtractor::openRTMP()
{

    RTMP_Init(rtmp);

    rtmp->Link.timeout = 5;
    rtmp->Link.lFlags |= RTMP_LF_LIVE;

#ifdef NDEBUG
    RTMP_LogSetLevel(RTMP_LOGERROR);
    RTMP_LogSetCallback(rtmp_log);
#endif

    int ret = RTMP_SetupURL(rtmp, (char *) url.c_str());

    if (!ret) {
        LOGE("rtmp setup url fail");
        return false;
    }

    ret = RTMP_Connect(rtmp, nullptr);

    if (!ret) {
        LOGE("rtmp connect %s fail", url.c_str());
        return false;
    }

    ret = RTMP_ConnectStream(rtmp, 0);

    if (!ret) {
        LOGE("rtmp connect stream fail");
        return false;
    }

    return true;
}


