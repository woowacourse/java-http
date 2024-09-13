package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    static Stream<Arguments> testDataForAuthenticateUser() {
        return Stream.of(
                Arguments.of(
                        Map.of("account", "gugu", "password", "password"),
                        new User("gugu", "password", "example@example.com")
                ),
                Arguments.of(Map.of("account", "gugu", "password", "wrongPassword"), null),
                Arguments.of(Map.of("account", "other", "password", "password"), null),
                Arguments.of(Map.of("account", "gugu"), null),
                Arguments.of(Map.of("password", "wrongPassword"), null),
                Arguments.of(Map.of(), null)
        );
    }

    static Stream<Arguments> testDataForSaveUser() {
        return Stream.of(
                Arguments.of(Map.of("account", "red", "password", "password"), true),
                Arguments.of(Map.of("account", "orange"), false)
        );
    }

    @DisplayName("사용자 인증을 해서, 성공하면 User를 반환하고, 실패하면 null을 반환한다.")
    @MethodSource("testDataForAuthenticateUser")
    @ParameterizedTest
    void testAuthenticateUser(final Map<String, String> userInformation, final User expected) {
        // given & when
        final User actual = userService.authenticateUser(userInformation);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Account와 Password로 사용자를 저장한다.")
    @MethodSource("testDataForSaveUser")
    @ParameterizedTest
    void testSaveUser(final Map<String, String> userInformation, final boolean isSaved) {
        // given & when
        userService.saveUser(userInformation);
        User user = InMemoryUserRepository.findByAccount(userInformation.get("account"));

        // then
        assertThat(Objects.nonNull(user)).isEqualTo(isSaved);
    }
}
