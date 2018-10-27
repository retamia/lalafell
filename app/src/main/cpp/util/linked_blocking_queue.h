//
// Created by retamia on 2018/10/24.
//

#ifndef LIVEPLAYER_LINKED_BLOCKING_QUEUE_H
#define LIVEPLAYER_LINKED_BLOCKING_QUEUE_H

#include <mutex>
#include <atomic>
#include <cstddef>

#define MAX_CAPACITY 256


template<typename T>
class LinkedBlockingQueue
{

public:
    LinkedBlockingQueue(size_t capacity = MAX_CAPACITY);
    LinkedBlockingQueue(const LinkedBlockingQueue &queue) = delete;
    virtual ~LinkedBlockingQueue();

    void enqueue(T ele);
    T dequeue();

    const T &first() const;

    const T &last() const;

    void clear();

    int size();

private:

    struct Node
    {
        explicit Node(const T &ele, Node *next)
            : value(ele), next(next)
        {};
        T value;
        Node *next = nullptr;
    };

    Node *firstNode;
    Node *lastNode;
    size_t capacity;
    std::mutex queueMutex;
    std::condition_variable notEmpty;
    std::condition_variable notFull;
    std::atomic_int queueSize;
};

template<typename T>
LinkedBlockingQueue<T>::LinkedBlockingQueue(size_t capacity)
    : queueSize(0), capacity(capacity), firstNode(nullptr), lastNode(nullptr)
{
}

template<typename T>
LinkedBlockingQueue<T>::~LinkedBlockingQueue()
{

    notFull.notify_all();
    notEmpty.notify_all();

    clear();
}

template<typename T>
void LinkedBlockingQueue<T>::enqueue(T ele)
{

    std::unique_lock<std::mutex> lock(queueMutex);

    if (queueSize == capacity) {
        notFull.wait(lock);
    }

    Node *prevNode = nullptr;
    Node *insertNode = firstNode;

    while (insertNode != nullptr) {
        prevNode = insertNode;
        insertNode = insertNode->next;
    }

    if (prevNode == nullptr) {
        prevNode = new Node(ele, nullptr);
        firstNode = prevNode;
    }
    else {
        prevNode->next = new Node(ele, nullptr);
    }

    queueSize++;

    notEmpty.notify_all();
}

template<typename T>
void LinkedBlockingQueue<T>::clear()
{

    std::lock_guard<std::mutex> lock(queueMutex);

    while (firstNode != nullptr) {
        Node *node = firstNode;
        firstNode = node->next;
        delete node;
    }

    notFull.notify_all();
}

template<typename T>
int LinkedBlockingQueue<T>::size()
{
    return queueSize;
}

template<typename T>
T LinkedBlockingQueue<T>::dequeue()
{

    std::unique_lock<std::mutex> lock(queueMutex);

    if (queueSize == 0) {
        notEmpty.wait(lock);
    }

    if (firstNode == nullptr) {
        nullptr;
    }

    Node *node = firstNode;
    T value = node->value;
    firstNode = node->next;
    queueSize--;

    delete node;

    notFull.notify_all();
    return value;
}

template<typename T>
const T &LinkedBlockingQueue<T>::first() const
{
    return firstNode->value;
}

template<typename T>
const T &LinkedBlockingQueue<T>::last() const
{
    return lastNode->value;
}

#endif //LIVEPLAYER_LINKED_BLOCKING_QUEUE_H
