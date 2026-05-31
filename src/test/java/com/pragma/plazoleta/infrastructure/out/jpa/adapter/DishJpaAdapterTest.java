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

    @Test
    @DisplayName(
            "Should return paginated active dishes sorted by name ascending when " +
            "category is provided in find active by restaurant and category paginated"
    )
    void shouldReturnPaginatedActiveDishesInFindActiveByRestaurantAndCategoryPaginated() {
        var baseDish = dishJpaAdapter.save(buildDish());
        var restaurant = baseDish.getRestaurant();
        var category = baseDish.getCategory();

        dishJpaAdapter.save(Dish.builder()
                .name("Apple Pizza")
                .description("Delicious apple pizza")
                .price(12000)
                .category(category)
                .restaurant(restaurant)
                .imageUrl("https://dishes.example.com/apple.png")
                .active(true)
                .build());

        dishJpaAdapter.save(Dish.builder()
                .name("Banana Pizza")
                .description("Inactive banana pizza")
                .price(10000)
                .category(category)
                .restaurant(restaurant)
                .imageUrl("https://dishes.example.com/banana.png")
                .active(false)
                .build());

        var result = dishJpaAdapter.findActiveByRestaurantAndCategoryPaginated(
                restaurant.getId(), category.getId(), 0, 10
        );

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Apple Pizza");
        assertThat(result.getItems().get(1).getName()).isEqualTo("Pineapple Pizza");
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(2L);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName(
            "Should return paginated active dishes sorted by name ascending when " +
            "category is null in find active by restaurant and category paginated"
    )
    void shouldReturnPaginatedDishesWhenCategoryIsNullInFindActiveByRestaurantAndCategoryPaginated() {
        var baseDish = dishJpaAdapter.save(buildDish());
        var restaurant = baseDish.getRestaurant();

        var newCategoryEntity = categoryRepository.save(categoryEntityMapper.toEntity(
                Category.builder()
                        .id(3L)
                        .name("Desserts")
                        .description("Sweet dishes")
                        .build()
        ));
        var newCategory = categoryEntityMapper.toModel(newCategoryEntity);

        dishJpaAdapter.save(Dish.builder()
                .name("Apple Pie")
                .description("Sweet apple pie")
                .price(8000)
                .category(newCategory)
                .restaurant(restaurant)
                .imageUrl("https://dishes.example.com/pie.png")
                .active(true)
                .build());

        var result = dishJpaAdapter.findActiveByRestaurantAndCategoryPaginated(
                restaurant.getId(), null, 0, 10
        );

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Apple Pie");
        assertThat(result.getItems().get(1).getName()).isEqualTo("Pineapple Pizza");
        assertThat(result.getTotalElements()).isEqualTo(2L);
    }

    @Test
    @DisplayName(
            "Should return empty paged result when " +
            "no active dishes match in find active by restaurant and category paginated"
    )
    void shouldReturnEmptyPagedResultWhenNoActiveDishesMatchInFindActiveByRestaurantAndCategoryPaginated() {
        var baseDish = buildDish();
        baseDish.setActive(false);
        var savedDish = dishJpaAdapter.save(baseDish);

        var result = dishJpaAdapter.findActiveByRestaurantAndCategoryPaginated(
                savedDish.getRestaurant().getId(), savedDish.getCategory().getId(), 0, 10
        );

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
    }
}
