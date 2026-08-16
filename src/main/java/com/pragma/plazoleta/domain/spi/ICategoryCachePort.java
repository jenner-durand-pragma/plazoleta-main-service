package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Category;

import java.util.Optional;

public interface ICategoryCachePort {
    Optional<Category> getCategoryById(Long id);
    void saveCategory(Category category);
}
