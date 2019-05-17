//
// Created by retamia on 2018/10/23.
//

#include "thread.h"

RThread::RThread()
    : t(nullptr)
{
    interruption = false;
}

RThread::~RThread()
{
    delete t;
    t = nullptr;
}

void RThread::start()
{
    if (t != nullptr) {
        return;
    }

    t = new std::thread(&RThread::run, this);
    pthread_setname_np(t->native_handle(), typeid(*this).name());
}

void RThread::msleep(int64_t ms)
{
    std::this_thread::sleep_for(std::chrono::milliseconds(ms));
}

void RThread::wait()
{
    if (t == nullptr) {
        return;
    }

    if (!t->joinable()) {
        return;
    }

    t->join();
}

void RThread::requestInterruption()
{
    this->interruption = true;
}

bool RThread::isInterruptionRequested()
{
    return interruption;
}
