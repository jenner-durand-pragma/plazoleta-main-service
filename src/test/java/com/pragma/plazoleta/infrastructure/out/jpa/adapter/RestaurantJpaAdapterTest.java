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
}
