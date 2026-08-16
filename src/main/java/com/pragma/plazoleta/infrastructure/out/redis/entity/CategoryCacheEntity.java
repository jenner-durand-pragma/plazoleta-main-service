package com.pragma.plazoleta.infrastructure.out.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("category")
public class CategoryCacheEntity {

    @Id
    private Long id;
    private String name;
    private String description;

    @TimeToLive
    private Long ttl;
}
