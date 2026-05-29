package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.ICategoryEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.ICategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ICategoryEntityMapperImpl.class)
class CategoryJpaAdapterTest {

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ICategoryEntityMapper categoryEntityMapper;

    private CategoryJpaAdapter categoryJpaAdapter;

    @BeforeEach
    void setUp() {
        categoryJpaAdapter = new CategoryJpaAdapter(categoryRepository, categoryEntityMapper);
    }

    private Category buildCategory() {
        return Category.builder()
                .id(1L)
                .name("Main Course")
                .description("Main dishes")
                .build();
    }

    @Test
    @DisplayName("Should return category when Id exists")
    void shouldReturnCategoryWhenIdExists() {
        var categoryEntity = categoryEntityMapper.toEntity(buildCategory());
        var saved = categoryRepository.save(categoryEntity);

        assertThat(categoryJpaAdapter.findById(saved.getId())).isNotNull();
        assertThat(categoryJpaAdapter.findById(10L)).isNull();
    }
}
