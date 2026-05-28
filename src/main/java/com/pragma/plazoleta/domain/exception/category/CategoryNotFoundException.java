package com.pragma.plazoleta.domain.exception.category;

import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.domain.model.Category;

public class CategoryNotFoundException extends NotFoundException {

    private static final String ERROR_MESSAGE = "Category not found.";

    public CategoryNotFoundException(Long categoryId) {
        super(ERROR_MESSAGE, Category.class.getSimpleName(), categoryId);
    }
}
