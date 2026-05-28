package com.pragma.plazoleta.infrastructure.out.feign.mapper;

import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.infrastructure.out.feign.dto.UserFeignResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IUserFeignMapper {

    UserInformation toUserInformation(UserFeignResponseDto dto);

}
