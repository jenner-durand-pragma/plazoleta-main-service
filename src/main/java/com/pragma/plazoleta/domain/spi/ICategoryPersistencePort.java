package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Category;

public interface ICategoryPersistencePort {

    Category findById(Long id);

}
