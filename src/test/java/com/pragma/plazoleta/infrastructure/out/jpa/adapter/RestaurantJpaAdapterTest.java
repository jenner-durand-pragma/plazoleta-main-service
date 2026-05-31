package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(IRestaurantEntityMapperImpl.class)
class RestaurantJpaAdapterTest {

    @Autowired
    private IRestaurantRepository restaurantRepository;

    @Autowired
    private IRestaurantEntityMapper restaurantEntityMapper;

    private RestaurantJpaAdapter restaurantJpaAdapter;

    @BeforeEach
    void setUp() {
        restaurantJpaAdapter = new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    private Restaurant buildRestaurant() {
        return Restaurant.builder()
                .name("Pizza Place")
                .address("Example Street 123")
                .ownerId(5L)
                .phone("+573005698325")
                .logoUrl("https://logo.example.com/image.png")
                .nit("9001234567")
                .build();
    }

    @Test
    @DisplayName("Should save a restaurant and return it with id")
    void shouldSaveRestaurant() {
        var saved = restaurantJpaAdapter.save(buildRestaurant());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNit()).isEqualTo("9001234567");
    }

    @Test
    @DisplayName("Should return true when NIT exists")
    void shouldReturnTrueWhenNitExists() {
        restaurantJpaAdapter.save(buildRestaurant());

        assertThat(restaurantJpaAdapter.existsByNit("9001234567")).isTrue();
        assertThat(restaurantJpaAdapter.existsByNit("0000000000")).isFalse();
    }

    @Test
    @DisplayName("Should return restaurant when Id exists")
    void shouldReturnRestaurantWhenIdExists() {
        var saved = restaurantJpaAdapter.save(buildRestaurant());

        assertThat(restaurantJpaAdapter.findById(saved.getId())).isNotNull();
        assertThat(restaurantJpaAdapter.findById(10L)).isNull();
    }

    @Test
    @DisplayName(
            "Should return paginated restaurants sorted by name ascending " +
            "when data exists in find all paginated by name asc"
    )
    void shouldReturnPaginatedRestaurantsSortedByNameAscendingWhenDataExistsInFindAllPaginatedByNameAsc() {
        restaurantJpaAdapter.save(Restaurant.builder()
                .name("Pizzería La Mamma")
                .address("Avenida Central 456")
                .ownerId(1L)
                .phone("+573001111111")
                .logoUrl("https://logo.example.com/pizza.png")
                .nit("9001111111")
                .build());

        restaurantJpaAdapter.save(Restaurant.builder()
                .name("Asados El Buen Gusto")
                .address("Calle Principal 123")
                .ownerId(2L)
                .phone("+573002222222")
                .logoUrl("https://logo.example.com/asados.png")
                .nit("9002222222")
                .build());

        restaurantJpaAdapter.save(Restaurant.builder()
                .name("Burger Master")
                .address("Carrera 45 # 12-34")
                .ownerId(3L)
                .phone("+573003333333")
                .logoUrl("https://logo.example.com/burger.png")
                .nit("9003333333")
                .build());

        var result = restaurantJpaAdapter.findAllPaginatedByNameAsc(0, 2);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Asados El Buen Gusto");
        assertThat(result.getItems().get(1).getName()).isEqualTo("Burger Master");
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3L);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return empty paged result when no data exists in find all paginated by name asc")
    void shouldReturnEmptyPagedResultWhenNoDataExistsInFindAllPaginatedByNameAsc() {
        var result = restaurantJpaAdapter.findAllPaginatedByNameAsc(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
    }
}
