package com.pragma.plazoleta.application.dto.request.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployeeRequestDto {

    @NotBlank(message = "Name is required")
    @Schema(description = "Owner's first name", example = "Jenner")
    private String name;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Owner's last name", example = "Durand")
    private String lastName;

    @NotBlank(message = "Document number is required")
    @Pattern(regexp = "^\\d+$", message = "Document number must be numeric only")
    @Schema(description = "Identity Document number (Numeric only)", example = "76859685")
    private String documentNumber;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^\\+?\\d{1,13}$",
            message = "Phone must be numeric, max 13 characters, optional leading '+'"
    )
    @Schema(description = "Contact phone number. Can include '+' symbol.", example = "+51985768594", maxLength = 13)
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Schema(description = "Valid email address", example = "jenner.durand@plazoleta.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "Plain text password.", example = "SecurePassword123!")
    private String password;

}
