package com.dto.userDTO;

import com.dto.material.MaterialSummaryDTO;

import java.util.List;

public record UserResponseDTO(Long id,
                              boolean admin,
                              String username,
                              String name,
                              String email,
                              String phoneNumber,
                              boolean isDeleted,
                              List<MaterialSummaryDTO>materials
                             ){
}
