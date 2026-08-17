package com.pragma.plazoleta.domain.service;

import com.pragma.plazoleta.domain.model.Category;

public interface ICategoryCacheService {

    Category getCategoryById(Long id);
    Category saveCategory(Category category);
}
