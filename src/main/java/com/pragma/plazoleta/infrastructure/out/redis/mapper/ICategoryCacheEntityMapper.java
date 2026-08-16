package com.pragma.plazoleta.infrastructure.out.redis.mapper;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.infrastructure.out.redis.entity.CategoryCacheEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICategoryCacheEntityMapper {

    CategoryCacheEntity toEntity(Category model);
    Category toModel(CategoryCacheEntity entity);
}
