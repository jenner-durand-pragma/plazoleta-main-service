package com.pragma.plazoleta.domain.service;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.service.impl.CategoryCacheServiceImpl;
import com.pragma.plazoleta.domain.spi.ICategoryCachePort;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryCacheServiceTest {

    @Mock
    private ICategoryCachePort categoryCachePort;

    @Mock
    private ICategoryPersistencePort categoryPersistencePort;

    private ICategoryCacheService categoryCacheService;

    @BeforeEach
    void setUp() {
        categoryCacheService = new CategoryCacheServiceImpl(
                categoryCachePort,
                categoryPersistencePort
        );
    }

    private Category buildCategory(Long id) {
        return Category.builder()
                .id(id)
                .name("Example category")
                .description("Example description")
                .build();
    }

    @ParameterizedTest
    @DisplayName("Should return correct category id successfully")
    @ValueSource(booleans = {true, false})
    void shouldReturnCorrectCategoryIdSuccessfully(boolean cacheExists) {
        var categoryId = 1L;
        var category = buildCategory(categoryId);

        if (cacheExists) {
            when(categoryCachePort.getCategoryById(categoryId))
                    .thenReturn(Optional.of(category));
        } else {
            when(categoryCachePort.getCategoryById(categoryId))
                    .thenReturn(Optional.empty());
            when(categoryPersistencePort.findById(categoryId))
                    .thenReturn(category);
        }

        var categoryReturned = categoryCacheService.getCategoryById(categoryId);

        assertNotNull(categoryReturned);
        assertEquals(category.getId(), categoryReturned.getId());
    }

    @Test
    @DisplayName("Should return category with id after saved")
    void shouldReturnCategoryWithIdAfterSaved() {
        var categoryId = 1L;
        var category = buildCategory(null);

        when(categoryPersistencePort.save(category))
                .thenAnswer(it -> {
                    Category categoryPassed = it.getArgument(0);
                    categoryPassed.setId(categoryId);

                    return categoryPassed;
                });

        var categoryReturned = categoryCacheService.saveCategory(category);

        assertNotNull(categoryReturned);
        assertEquals(categoryId, categoryReturned.getId());
    }
}
