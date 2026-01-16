package com.example.demo.service;

import com.dto.userDTO.UserRequestDTO;
import com.dto.userDTO.UserResponseDTO;
import com.dto.userDTO.UserUpdateRequestDTO;
import com.exception.ResourceNotFound;
import com.mapper.UserMapper;
import com.model.Material;
import com.model.User;
import com.repository.UserRepository;
import com.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserService userService;

    private User user;
    private Material material;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserService(userRepository, userMapper);

        material = Material.builder()
                .id(1L)
                .name("Book Title")
                .build();

        user = User.builder()
                .id(1L)
                .username("ivan123")
                .name("Ivan")
                .email("ivan@example.com")
                .phoneNumber("123456789")
                .isAdmin(false)
                .deleted(false)
                .materials(Set.of(material))
                .build();
    }

    @Test
    void createUser_createsUserSuccessfully() {
        UserRequestDTO request = new UserRequestDTO("ivan123", "password", "Ivan", "ivan@example.com", "123456789");
        UserResponseDTO responseDTO = new UserResponseDTO(
                1L, false, "ivan123", "Ivan", "ivan@example.com", "123456789", false, List.of()
        );

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("ivan123", result.username());
        assertEquals("Ivan", result.name());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toUserResponseDTO(any(User.class));
    }

    @Test
    void getAllUsers_returnsList() {
        UserResponseDTO responseDTO = new UserResponseDTO(
                1L, false, "ivan123", "Ivan", "ivan@example.com", "123456789", false, List.of()
        );

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserResponseDTOList(List.of(user))).thenReturn(List.of(responseDTO));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("ivan123", result.get(0).username());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toUserResponseDTOList(anyList());
    }

    @Test
    void getUserById_returnsUser_whenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_throwsException_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> userService.getUserById(99L));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void updateUser_updatesUserSuccessfully() {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO("ivanUpdated", "Ivan Updated", "newPass", "ivanNew@example.com", "987654321", null);
        UserResponseDTO responseDTO = new UserResponseDTO(
                1L, false, "ivanUpdated", "Ivan Updated", "ivanNew@example.com", "987654321", false, List.of()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateUser(1L, request);

        assertNotNull(result);
        assertEquals("ivanUpdated", result.username());
        assertEquals("Ivan Updated", result.name());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toUserResponseDTO(user);
    }

    @Test
    void updateUser_throwsException_whenNotFound() {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO("ivanUpdated", "Ivan Updated", null, null, null, null);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> userService.updateUser(99L, request));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void deleteUser_deletesUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertTrue(user.isDeleted());
        user.getMaterials().forEach(material -> assertTrue(material.isDeleted()));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void deleteUser_throwsException_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> userService.deleteUser(99L));
        verify(userRepository, times(1)).findById(99L);
    }
}
