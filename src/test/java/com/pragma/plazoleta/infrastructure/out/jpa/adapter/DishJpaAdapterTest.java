package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.ICategoryEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IDishEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.ICategoryRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ IDishEntityMapperImpl.class, ICategoryEntityMapperImpl.class, IRestaurantEntityMapperImpl.class })
class DishJpaAdapterTest {

    @Autowired
    private IDishRepository dishRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private IRestaurantRepository restaurantRepository;

    @Autowired
    private IDishEntityMapper dishEntityMapper;

    @Autowired
    private ICategoryEntityMapper categoryEntityMapper;

    @Autowired
    private IRestaurantEntityMapper restaurantEntityMapper;

    private DishJpaAdapter dishJpaAdapter;

    @BeforeEach
    void setUp() {
        dishJpaAdapter = new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    private Dish buildDish() {
        var categoryReference = Category.builder()
                .id(1L)
                .name("Main Course")
                .description("Main dishes")
                .build();
        var savedCategoryEntity = categoryRepository.save(categoryEntityMapper.toEntity(categoryReference));
        categoryReference.setId(savedCategoryEntity.getId());

        var restaurantReference = Restaurant.builder()
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();
        var savedRestaurantEntity = restaurantRepository.save(restaurantEntityMapper.toEntity(restaurantReference));
        restaurantReference.setId(savedRestaurantEntity.getId());

        return Dish.builder()
                .name("Pineapple Pizza")
                .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                .price(15000)
                .category(categoryReference)
                .restaurant(restaurantReference)
                .imageUrl("https://dishes.example.com/dish.png")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should save a dish and return it with id")
    void shouldSaveDish() {
        var saved = dishJpaAdapter.save(buildDish());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Pineapple Pizza");
    }

    @Test
    @DisplayName("Should return dish when Id exists")
    void shouldReturnDishWhenIdExists() {
        var saved = dishJpaAdapter.save(buildDish());

        assertThat(dishJpaAdapter.findById(saved.getId())).isNotNull();
        assertThat(dishJpaAdapter.findById(10L)).isNull();
    }
}
