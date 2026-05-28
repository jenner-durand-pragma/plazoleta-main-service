package com.pragma.plazoleta.domain.model;

import lombok.*;

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
    private String roleName;
}
