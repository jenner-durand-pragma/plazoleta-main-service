package com.pragma.plazoleta.infrastructure.out.redis.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.spi.ICategoryCachePort;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.ICategoryCacheEntityMapper;
import com.pragma.plazoleta.infrastructure.out.redis.repository.ICategoryCacheRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CategoryCacheAdapter implements ICategoryCachePort {

    private final ICategoryCacheRepository categoryCacheRepository;
    private final ICategoryCacheEntityMapper categoryCacheEntityMapper;

    private static final long TTL_INDIVIDUAL_SECONDS = 3600;

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return null;
    }

    @Override
    public void saveCategory(Category category) {
    }
}
