package com.dto.userDTO;

public record UserUpdateRequestDTO(String username,
                                   String name,
                                   String password,
                                   String email,
                                   String phoneNumber,
                                   String address) {


}
