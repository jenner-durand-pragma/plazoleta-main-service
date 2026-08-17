package com.pragma.plazoleta.domain.service.impl;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.service.ICategoryCacheService;
import com.pragma.plazoleta.domain.spi.ICategoryCachePort;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CategoryCacheServiceImpl implements ICategoryCacheService {

    private final ICategoryCachePort categoryCachePort;
    private final ICategoryPersistencePort categoryPersistencePort;

    @Override
    public Category getCategoryById(Long id) {
        return categoryCachePort.getCategoryById(id)
                .orElseGet(() -> categoryPersistencePort.findById(id));
    }

    @Override
    public Category saveCategory(Category category) {
        var categoryCache = categoryPersistencePort.save(category);
        categoryCachePort.saveCategory(categoryCache);

        return categoryCache;
    }
}
