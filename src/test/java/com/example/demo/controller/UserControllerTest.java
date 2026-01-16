package com.example.demo.controller;

import com.controller.UserController;
import com.dto.userDTO.UserRequestDTO;
import com.dto.userDTO.UserResponseDTO;
import com.dto.userDTO.UserUpdateRequestDTO;
import com.exception.ResourceNotFound;
import com.mapper.UserMapper;
import com.model.User;
import com.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllUsers_returnsListOfUsers() throws Exception {
        UserResponseDTO user1 = new UserResponseDTO(
                1L, false, "ivan123", "Ivan Ivanov",
                "ivan@test.com", "0888123456", false, List.of()
        );
        UserResponseDTO user2 = new UserResponseDTO(
                2L, true, "admin", "Admin",
                "admin@test.com", "0000000000", false, List.of()
        );

        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("ivan123"))
                .andExpect(jsonPath("$[1].admin").value(true));
    }

    @Test
    void getUserById_returnsUser() throws Exception {
        Long id = 1L;

        User user = User.builder()
                .id(id)
                .username("ivan123")
                .build();

        UserResponseDTO responseDTO = new UserResponseDTO(
                id, false, "ivan123", "Ivan Ivanov",
                "ivan@test.com", "0888123456", false, List.of()
        );

        Mockito.when(userService.getUserById(id)).thenReturn(user);
        Mockito.when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ivan123"))
                .andExpect(jsonPath("$.email").value("ivan@test.com"));
    }

    @Test
    void getUserById_whenNotFound_returns404() throws Exception {
        Long id = 1L;

        Mockito.when(userService.getUserById(id))
                .thenThrow(new ResourceNotFound(User.class, id));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    void createUser_returnsCreatedUser() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO(
                "ivan123",
                "Ivan Ivanov",
                "password",
                "ivan@test.com",
                "0888123456"
        );

        UserResponseDTO responseDTO = new UserResponseDTO(
                1L, false, "ivan123", "Ivan Ivanov",
                "ivan@test.com", "0888123456", false, List.of()
        );

        Mockito.when(userService.createUser(any(UserRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("ivan123"));
    }

    @Test
    void updateUser_returnsUpdatedUser() throws Exception {
        Long id = 1L;

        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO(
                "newUsername",
                "New Name",
                "newPassword",
                "new@test.com",
                "0999999999",
                "Plovdiv"
        );

        UserResponseDTO responseDTO = new UserResponseDTO(
                id, false, "newUsername", "New Name",
                "new@test.com", "0999999999", false, List.of()
        );

        Mockito.when(userService.updateUser(id, updateRequest))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUsername"))
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void deleteUser_returnsNoContent() throws Exception {
        Long id = 1L;

        Mockito.doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
    }
}

