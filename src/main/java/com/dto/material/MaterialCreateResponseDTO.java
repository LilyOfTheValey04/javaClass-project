package com.dto.material;


import java.util.Set;

public record MaterialCreateResponseDTO(String name,
                                        String description,
                                        Double price,
                                        Integer quantity,
                                        String author,
                                        String ownerUsername,
                                        Set<String> categoryNames) {
}
