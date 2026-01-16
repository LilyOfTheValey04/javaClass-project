package com.example.demo.mapper;

import com.dto.userDTO.UserResponseDTO;
import com.mapper.UserMapper;
import com.model.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
public class UserMapperTest {

    private final UserMapper underTest = Mappers.getMapper(UserMapper.class);

    private static Stream<Arguments> userProvider() {
        return Stream.of(
                Arguments.of(
                        User.builder()
                                .id(1L)
                                .username("john_doe")
                                .name("John Doe")
                                .email("john@test.com")
                                .phoneNumber("0888123456")
                                .deleted(false)
                                .materials(null)
                                .build(),
                        false
                ),
                Arguments.of(
                        User.builder()
                                .id(2L)
                                .username("admin")
                                .name(null)
                                .email(null)
                                .phoneNumber(null)
                                .deleted(true)
                                .materials(null)
                                .build(),
                        true
                )
        );
    }

    @ParameterizedTest
    @MethodSource("userProvider")
    void toUserResponseDTOTest(User user, boolean expectedDeleted) {

        UserResponseDTO result = underTest.toUserResponseDTO(user);

        assertThat(result).isNotNull();

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.username()).isEqualTo(user.getUsername());
        assertThat(result.name()).isEqualTo(user.getName());
        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.phoneNumber()).isEqualTo(user.getPhoneNumber());

        assertThat(result.admin()).isFalse();
        assertThat(result.isDeleted()).isEqualTo(expectedDeleted);

        if (user.getMaterials() == null) {
            assertThat(result.materials()).isNull();
        } else {
            assertThat(result.materials()).isNotNull();
            assertThat(result.materials()).hasSize(user.getMaterials().size());
        }
    }

    private static Stream<Arguments> userListProvider() {
        List<User> users = List.of(
                User.builder()
                        .id(1L)
                        .username("john_doe")
                        .name("John Doe")
                        .email("john@test.com")
                        .phoneNumber("0888123456")
                        .deleted(false)
                        .materials(null)
                        .build(),
                User.builder()
                        .id(2L)
                        .username("alice")
                        .name("Alice")
                        .email("alice@test.com")
                        .phoneNumber(null)
                        .deleted(true)
                        .materials(null)
                        .build()
        );

        return Stream.of(Arguments.of(users));
    }

    @ParameterizedTest
    @MethodSource("userListProvider")
    void toUserResponseDTOListTest(List<User> users) {
        List<UserResponseDTO> result = underTest.toUserResponseDTOList(users);

        assertThat(result).hasSize(users.size());

        for (int i = 0; i < users.size(); i++) {
            User source = users.get(i);
            UserResponseDTO dto = result.get(i);

            assertThat(dto.id()).isEqualTo(source.getId());
            assertThat(dto.username()).isEqualTo(source.getUsername());
            assertThat(dto.name()).isEqualTo(source.getName());
            assertThat(dto.email()).isEqualTo(source.getEmail());
            assertThat(dto.phoneNumber()).isEqualTo(source.getPhoneNumber());

            assertThat(dto.admin()).isFalse();
            assertThat(dto.isDeleted()).isEqualTo(source.isDeleted());

            if (source.getMaterials() == null) {
                assertThat(dto.materials()).isNull();
            } else {
                assertThat(dto.materials()).hasSize(source.getMaterials().size());
            }
        }
    }
}
