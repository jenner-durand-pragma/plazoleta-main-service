package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.employee.CreateEmployeeRequestDto;
import com.pragma.plazoleta.domain.model.UserInformation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IEmployeeRequestMapper {

    UserInformation toUserInformation(CreateEmployeeRequestDto dto);
}
