package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.model.RestaurantEmployee;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEmployeeEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEmployeeEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(IRestaurantEmployeeEntityMapperImpl.class)
class RestaurantEmployeeJpaAdapterTest {

    @Autowired
    private IRestaurantEmployeeRepository restaurantEmployeeRepository;

    @Autowired
    private IRestaurantEmployeeEntityMapper restaurantEmployeeEntityMapper;

    private RestaurantEmployeeJpaAdapter restaurantEmployeeJpaAdapter;

    @BeforeEach
    void setUp() {
        restaurantEmployeeJpaAdapter = new RestaurantEmployeeJpaAdapter(
                restaurantEmployeeRepository,
                restaurantEmployeeEntityMapper
        );
    }

    private RestaurantEmployee buildRestaurantEmployee() {
        return RestaurantEmployee.builder()
                .userId(1L)
                .restaurantId(2L)
                .build();
    }

    @Test
    @DisplayName("Should save a restaurant employee and return it with its ids")
    void shouldSaveRestaurantEmployee() {
        var saved = restaurantEmployeeJpaAdapter.save(buildRestaurantEmployee());

        assertThat(saved).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getRestaurantId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should return restaurant id when user id exists")
    void shouldReturnRestaurantIdWhenUserIdExists() {
        restaurantEmployeeJpaAdapter.save(buildRestaurantEmployee());

        assertThat(restaurantEmployeeJpaAdapter.findRestaurantIdByUserId(1L)).isEqualTo(Optional.of(2L));
        assertThat(restaurantEmployeeJpaAdapter.findRestaurantIdByUserId(2L)).isEmpty();
    }
}
