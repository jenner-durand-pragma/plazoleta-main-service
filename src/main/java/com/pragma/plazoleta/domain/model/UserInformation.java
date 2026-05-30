package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInformation {

    private Long id;
    private String name;
    private String lastName;
    private String documentNumber;
    private String phone;
    private String email;
    private String password;
    private String roleName;
}
