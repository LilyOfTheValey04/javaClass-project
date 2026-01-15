package com.service;

import com.dto.userDTO.UserRequestDTO;
import com.dto.userDTO.UserResponseDTO;
import com.exception.ResourceNotFound;
import com.mapper.UserMapper;
import com.model.User;
import com.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        User user = User.builder()
                .username(request.username())
                .name(request.name())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .isAdmin(false) // default
                .deleted(false)
                // passwordHash трябва да се хешира, тук го оставям директно за пример
                .passwordHash(request.password())
                .build();

        userRepository.save(user);

        return userMapper.toUserResponseDTO(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponseDTOList(users);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(User.class, id));
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(User.class, id));

        if (request.username() != null) user.setUsername(request.username());
        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber());
        if (request.password() != null) user.setPasswordHash(request.password());

        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(User.class, id));
        userRepository.delete(user);
    }


}
