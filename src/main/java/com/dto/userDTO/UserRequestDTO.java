package com.dto.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(@NotBlank String username,
                             @NotBlank String password,
                             String name,
                             @Email String email,
                             String phoneNumber) {


}
