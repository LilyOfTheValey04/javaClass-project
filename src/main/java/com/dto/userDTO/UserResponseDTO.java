package com.dto.userDTO;

public record UserResponseDTO(Long id,
                              boolean admin,
                              String username,
                              String name,
                              String email,
                              String phoneNumber
                             // List<MaterialCreateRequestDTO> purchases, //purchased material //fix dto
                            //  List<MaterialCreateRequestDTO> materials //all created materials //fix dto
                             ){
}
