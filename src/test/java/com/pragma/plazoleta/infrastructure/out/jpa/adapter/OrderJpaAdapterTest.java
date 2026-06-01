package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.enums.OrderStatus;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.CategoryEntity;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.RestaurantEntity;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.ICategoryEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IDishEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IOrderEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.ICategoryRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IOrderRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        IOrderEntityMapperImpl.class,
        IDishEntityMapperImpl.class,
        ICategoryEntityMapperImpl.class,
        IRestaurantEntityMapperImpl.class
})
class OrderJpaAdapterTest {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IOrderEntityMapper orderEntityMapper;

    @Autowired
    private IRestaurantRepository restaurantRepository;

    @Autowired
    private IDishRepository dishRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    private OrderJpaAdapter orderJpaAdapter;
    private RestaurantEntity savedRestaurant;
    private DishEntity savedDish;

    @BeforeEach
    void setUp() {
        orderJpaAdapter = new OrderJpaAdapter(orderRepository, orderEntityMapper, restaurantRepository, dishRepository);

        var category = categoryRepository.save(
                CategoryEntity.builder()
                        .id(1L)
                        .name("Main Course")
                        .description("Main dishes")
                        .build()
        );

        savedRestaurant = restaurantRepository.save(
                RestaurantEntity.builder()
                        .name("Pizza Place")
                        .address("Example Street 123")
                        .ownerId(5L)
                        .phone("+573005698325")
                        .logoUrl("https://logo.example.com/image.png")
                        .nit("9001234567")
                        .build()
        );

        savedDish = dishRepository.save(
                DishEntity.builder()
                        .name("Pineapple Pizza")
                        .description("Classic Hawaiian pizza featuring a perfect balance of sweet juicy pineapple chunks")
                        .price(15000)
                        .imageUrl("https://dishes.example.com/dish.png")
                        .category(category)
                        .restaurant(savedRestaurant)
                        .active(true)
                        .build()
        );
    }

    @Test
    @DisplayName("Should save order and items successfully when data is valid in save")
    void shouldSaveOrderAndItemsSuccessfullyWhenDataIsValidInSave() {
        var domainOrder = Order.builder()
                .clientId(5L)
                .restaurant(Restaurant.builder().id(savedRestaurant.getId()).build())
                .items(List.of(
                        OrderDish.builder()
                                .dishId(savedDish.getId())
                                .dish(Dish.builder().id(savedDish.getId()).build())
                                .quantity(2)
                                .build()
                ))
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .build();

        var saved = orderJpaAdapter.save(domainOrder);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(saved.getItems().get(0).getDishId()).isEqualTo(savedDish.getId());
        assertThat(saved.getItems().get(0).getDish().getId()).isEqualTo(savedDish.getId());
        assertThat(saved.getItems().get(0).getOrderId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Should return true when client has active orders in exists active order by client id")
    void shouldReturnTrueWhenClientHasActiveOrdersInExistsActiveOrderByClientId() {
        for (var activeStatus : OrderStatus.ACTIVE_STATUSES) {
            var clientId = 100L + activeStatus.ordinal();
            persistOrder(clientId, activeStatus);

            assertThat(orderJpaAdapter.existsActiveOrderByClientId(clientId)).isTrue();
        }
    }

    @Test
    @DisplayName("Should return false when client has no orders in exists active order by client id")
    void shouldReturnFalseWhenClientHasNoOrdersInExistsActiveOrderByClientId() {
        var clientId = 200L;

        assertThat(orderJpaAdapter.existsActiveOrderByClientId(clientId)).isFalse();
    }

    @Test
    @DisplayName("Should return false when client only has inactive orders in exists active order by client id")
    void shouldReturnFalseWhenClientOnlyHasInactiveOrdersInExistsActiveOrderByClientId() {
        var clientId = 200L;
        persistOrder(clientId, OrderStatus.DELIVERED);
        persistOrder(clientId, OrderStatus.CANCELLED);

        assertThat(orderJpaAdapter.existsActiveOrderByClientId(clientId)).isFalse();
    }

    @Test
    @DisplayName(
            "Should return paginated orders sorted by date ascending when " +
            "orders match status in find by restaurant id and status"
    )
    void shouldReturnPaginatedOrdersSortedByDateAscendingWhenOrdersMatchStatusInFindByRestaurantIdAndStatus() {
        persistOrder(101L, OrderStatus.PENDING, LocalDateTime.now().minusHours(2));
        persistOrder(102L, OrderStatus.PENDING, LocalDateTime.now().minusHours(1));
        persistOrder(103L, OrderStatus.READY, LocalDateTime.now());

        var result = orderJpaAdapter.findByRestaurantIdAndStatus(
                savedRestaurant.getId(),
                OrderStatus.PENDING,
                0,
                10
        );

        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getClientId()).isEqualTo(101L);
        assertThat(result.getItems().get(1).getClientId()).isEqualTo(102L);
    }

    @Test
    @DisplayName("Should return empty paged result when no orders match status in find by restaurant id and status")
    void shouldReturnEmptyPagedResultWhenNoOrdersMatchStatusInFindByRestaurantIdAndStatus() {
        persistOrder(101L, OrderStatus.PENDING);

        var result = orderJpaAdapter.findByRestaurantIdAndStatus(
                savedRestaurant.getId(),
                OrderStatus.DELIVERED,
                0,
                10
        );

        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Should return order when id exists")
    void shouldReturnOrderWhenIdExists() {
        var clientId = 101L;
        var saved = persistOrder(clientId, OrderStatus.PENDING);

        assertThat(orderJpaAdapter.findById(saved.getId())).isNotNull();
        assertThat(orderJpaAdapter.findById(10L)).isEmpty();
    }

    private Order persistOrder(Long clientId, OrderStatus status, LocalDateTime datetime) {
        var order = Order.builder()
                .clientId(clientId)
                .restaurant(Restaurant.builder().id(savedRestaurant.getId()).build())
                .items(List.of(OrderDish.builder()
                        .dishId(savedDish.getId())
                        .dish(Dish.builder().id(savedDish.getId()).build())
                        .quantity(1)
                        .build()))
                .status(status)
                .orderDate(
                        datetime == null
                                ? LocalDateTime.now()
                                : datetime
                )
                .build();

        return orderJpaAdapter.save(order);
    }

    private Order persistOrder(Long clientId, OrderStatus status) {
        return persistOrder(clientId, status, null);
    }
}