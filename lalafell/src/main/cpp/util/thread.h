//
// Created by retamia on 2018/10/23.
//

#ifndef LIVEPLAYER_THREAD_H
#define LIVEPLAYER_THREAD_H

#include <atomic>
#include <thread>

class RThread {
public:
    explicit RThread();
    virtual ~RThread();
    void start();
    void wait();
    void msleep(int64_t ms);
    void requestInterruption();
    bool isInterruptionRequested();

protected:
    virtual void run() = 0;

private:
    std::atomic_bool interruption;
    std::thread *t;
};


#endif //LIVEPLAYER_THREAD_H
