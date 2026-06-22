package com.pragma.plazoleta.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IEmployeeServicePort;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.api.IOrderTraceabilityServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.INotificationPort;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.IOrderTraceabilityPort;
import com.pragma.plazoleta.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import com.pragma.plazoleta.domain.usecase.EmployeeUseCase;
import com.pragma.plazoleta.domain.usecase.OrderTraceabilityUseCase;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import com.pragma.plazoleta.infrastructure.configuration.security.token.ITokenValidationPort;
import com.pragma.plazoleta.infrastructure.out.feign.adapter.NotificationAdapter;
import com.pragma.plazoleta.infrastructure.out.feign.adapter.OrderTraceabilityAdapter;
import com.pragma.plazoleta.infrastructure.out.feign.adapter.UserInformationAdapter;
import com.pragma.plazoleta.infrastructure.out.feign.client.INotificationFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.client.IOrderTraceabilityFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IOrderTraceabilityFeignMapper;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IUserFeignMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.adapter.CategoryJpaAdapter;
import com.pragma.plazoleta.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.pragma.plazoleta.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import com.pragma.plazoleta.infrastructure.out.jpa.adapter.RestaurantEmployeeJpaAdapter;
import com.pragma.plazoleta.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.ICategoryEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEmployeeEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.ICategoryRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IOrderRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantEmployeeRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import com.pragma.plazoleta.infrastructure.out.security.jwt.JwtAdapter;
import com.pragma.plazoleta.infrastructure.out.security.jwt.configuration.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    private final ICategoryRepository categoryRepository;
    private final ICategoryEntityMapper categoryEntityMapper;

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    private final IUserFeignClient userFeignClient;
    private final IUserFeignMapper userFeignMapper;

    private final IRestaurantEmployeeRepository restaurantEmployeeRepository;
    private final IRestaurantEmployeeEntityMapper restaurantEmployeeEntityMapper;

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    private final INotificationFeignClient notificationFeignClient;

    private final IOrderTraceabilityFeignClient orderTraceabilityFeignClient;
    private final IOrderTraceabilityFeignMapper orderTraceabilityFeignMapper;

    private final JwtProperties jwtProperties;

    @Bean
    public IUserInformationPort userValidationPort(
            ObjectMapper objectMapper
    ) {
        return new UserInformationAdapter(
                userFeignClient,
                userFeignMapper,
                objectMapper
        );
    }

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public ICategoryPersistencePort categoryPersistencePort() {
        return new CategoryJpaAdapter(categoryRepository, categoryEntityMapper);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort(
            ObjectMapper objectMapper
    ) {
        return new RestaurantUseCase(restaurantPersistencePort(), userValidationPort(objectMapper));
    }

    @Bean
    public IDishServicePort dishServicePort() {
        return new DishUseCase(dishPersistencePort(), restaurantPersistencePort(), categoryPersistencePort());
    }

    @Bean
    public ITokenValidationPort tokenValidationPort() {
        return new JwtAdapter(jwtProperties);
    }

    @Bean
    public IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort() {
        return new RestaurantEmployeeJpaAdapter(restaurantEmployeeRepository, restaurantEmployeeEntityMapper);
    }

    @Bean
    public IUserInformationPort userInformationPort(
            ObjectMapper objectMapper
    ) {
        return new UserInformationAdapter(userFeignClient, userFeignMapper, objectMapper);
    }

    @Bean
    public IEmployeeServicePort employeeServicePort(
            ObjectMapper objectMapper
    ) {
        return new EmployeeUseCase(
                restaurantPersistencePort(),
                restaurantEmployeePersistencePort(),
                userInformationPort(objectMapper)
        );
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderEntityMapper, restaurantRepository, dishRepository);
    }

    @Bean
    public INotificationPort notificationPort() {
        return new NotificationAdapter(notificationFeignClient);
    }

    @Bean
    public IOrderTraceabilityPort orderTraceabilityPort(
            ObjectMapper objectMapper
    ) {
        return new OrderTraceabilityAdapter(
                orderTraceabilityFeignClient,
                userInformationPort(objectMapper),
                orderTraceabilityFeignMapper
        );
    }

    @Bean
    public IOrderServicePort orderServicePort(
            ObjectMapper objectMapper
    ) {
        return new OrderUseCase(
                orderPersistencePort(),
                restaurantPersistencePort(),
                dishPersistencePort(),
                restaurantEmployeePersistencePort(),
                notificationPort(),
                userInformationPort(objectMapper),
                orderTraceabilityPort(objectMapper)
        );
    }

    @Bean
    public IOrderTraceabilityServicePort orderTraceabilityServicePort(
            ObjectMapper objectMapper
    ) {
        return new OrderTraceabilityUseCase(
                orderTraceabilityPort(objectMapper),
                orderPersistencePort()
        );
    }
}
