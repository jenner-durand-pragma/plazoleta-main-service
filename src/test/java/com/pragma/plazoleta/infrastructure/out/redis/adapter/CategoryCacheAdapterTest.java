package com.pragma.plazoleta.infrastructure.out.redis.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.infrastructure.out.redis.container.EmbeddedRedisExtension;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.ICategoryCacheEntityMapper;
import com.pragma.plazoleta.infrastructure.out.redis.mapper.ICategoryCacheEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.redis.repository.ICategoryCacheRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import({
        ICategoryCacheEntityMapperImpl.class
})
public class CategoryCacheAdapterTest {

    private static EmbeddedRedisExtension redisExtension;

    @BeforeAll
    static void start() throws IOException {
        redisExtension = new EmbeddedRedisExtension();
        redisExtension.start();
    }

    @AfterAll
    static void stop() throws IOException {
        redisExtension.stop();
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", () -> redisExtension.getHost());
        registry.add("spring.redis.port", () -> redisExtension.getPort());
    }

    @Autowired
    private ICategoryCacheEntityMapper categoryCacheEntityMapper;

    @Autowired
    private ICategoryCacheRepository categoryCacheRepository;

    private CategoryCacheAdapter categoryCacheAdapter;

    @BeforeEach
    void setUp() {
        categoryCacheAdapter = new CategoryCacheAdapter(
                categoryCacheRepository,
                categoryCacheEntityMapper
        );
    }

    @Test
    @DisplayName("Should return category saved successfully")
    void shouldReturnCategorySavedSuccessfully() {
        var category = Category.builder()
                .id(1L)
                .name("Main Course")
                .description("Main dishes")
                .build();

        categoryCacheAdapter.saveCategory(category);

        var categoryFound = categoryCacheAdapter.getCategoryById(category.getId());

        assertTrue(categoryFound.isPresent());
        assertEquals(category.getId(), categoryFound.get().getId());
        assertEquals(category.getName(), categoryFound.get().getName());
    }
}
