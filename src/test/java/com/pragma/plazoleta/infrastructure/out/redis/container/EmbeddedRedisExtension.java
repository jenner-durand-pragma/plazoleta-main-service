package com.pragma.plazoleta.infrastructure.out.redis.container;

import redis.embedded.RedisServer;

import java.io.IOException;

public class EmbeddedRedisExtension {

    private static final int REDIS_PORT = 6370;
    private static final String REDIS_HOST = "localhost";

    private final RedisServer redisServer;

    public EmbeddedRedisExtension() throws IOException {
        this.redisServer = RedisServer.newRedisServer()
                .port(REDIS_PORT)
                .setting("maxmemory 128M")
                .build();
    }

    public void start() throws IOException {
        redisServer.start();
    }

    public void stop() throws IOException {
        redisServer.stop();
    }

    public Integer getPort() {
        return REDIS_PORT;
    }

    public String getHost() {
        return REDIS_HOST;
    }
}
