cmake_minimum_required(VERSION 3.4.1)

project(rtmp)

add_definitions(-DNO_CRYPTO)

set(LIB_RTMP_HEADERS
        amf.h
        bytes.h
        dh.h
        dhgroups.h
        handshake.h
        http.h
        log.h
        rtmp.h
        rtmp_sys.h
        )

set(LIB_RTMP_SOURCES
        amf.c
        hashswf.c
        log.c
        parseurl.c
        rtmp.c
        )

add_library(${PROJECT_NAME} SHARED
        ${LIB_RTMP_HEADERS}
        ${LIB_RTMP_SOURCES})

# target_link_libraries(${PROJECT_NAME} ssl crypto)