package com.mapper;

import com.dto.userDTO.UserResponseDTO;
import com.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "materials", source = "materials")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "admin", constant = "false")
    @Mapping(target = "isDeleted", source = "deleted")

    UserResponseDTO toUserResponseDTO(User user);

    List<UserResponseDTO> toUserResponseDTOList(List<User> users);
}
