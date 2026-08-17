package com.pragma.plazoleta.infrastructure.out.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("dish")
public class DishCacheEntity implements Serializable {

    @Id
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;
    private Boolean active;

    @TimeToLive
    private Long ttl;
}
