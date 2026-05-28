package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.infrastructure.out.jpa.entity.RoleEntity;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RoleJpaAdapterTest {
    @Autowired
    private IRoleRepository roleRepository;

    @Spy
    private IRoleEntityMapper roleEntityMapper = Mappers.getMapper(IRoleEntityMapper.class);

    private RoleJpaAdapter roleJpaAdapter;

    @BeforeEach
    void setUp() {
        roleJpaAdapter = new RoleJpaAdapter(roleRepository, roleEntityMapper);

        var roleEntity = RoleEntity.builder()
                .id(2L)
                .name("OWNER")
                .description("Restaurant owner")
                .build();
        roleRepository.save(roleEntity);
    }


    @Test
    @DisplayName("Should save a user and return it with a generated id")
    void shouldSaveUserAndReturnWithId() {
        var saved = roleJpaAdapter.findByName("OWNER");

        assertThat(saved).isNotNull();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("OWNER");
    }
}
