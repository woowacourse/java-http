package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService();
    }

    @DisplayName("유저 정보를 조회하면 로그에 유저 정보가 기록된다..")
    @Test
    void findUser() {
        //given
        Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();
        String account = "gugu";
        String password = "password";
        Map<String, String> params = Map.of("account", account, "password", password);

        //when
        User result = userService.findUser(params);
        String expectedLogs = "user : " + result;

        //then
        List<ILoggingEvent> testLogs = listAppender.list;
        String formattedMessage = testLogs.getFirst().getFormattedMessage();
        assertAll(
                () -> assertThat(result.getAccount()).isEqualTo(account),
                () -> assertThat(result.getPassword()).isEqualTo(password),
                () -> assertThat(formattedMessage).isEqualTo(expectedLogs)
        );
    }

    @DisplayName("유저 조회시 유저 정보가 존재하지 않으면 예외가 발생한다.")
    @Test
    void findUserWithInvalidAccount() {
        //given
        String account = UUID.randomUUID().toString();
        String password = "password";
        Map<String, String> params = Map.of("account", account, "password", password);

        //when //then
        assertThatThrownBy(() -> userService.findUser(params))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저 정보가 존재하지 않습니다.");
    }

    @DisplayName("유저 조회시 비밀번호가 일치하지 않으면 예외가 발생한다.")
    @Test
    void findUserWithInvalidPassword() {
        //given
        String account = "gugu";
        String password = "pass";
        Map<String, String> params = Map.of("account", account, "password", password);
        //when //then
        assertThatThrownBy(() -> userService.findUser(params))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("유저 정보를 생성한다.")
    @Test
    void create() {
        //given
        String account = UUID.randomUUID().toString();
        String password = "password";
        String email = "test@email.com";
        Map<String, String> params = Map.of(
                "account", account,
                "password", password,
                "email", email
        );

        //when
        userService.create(params);
        Optional<User> result = InMemoryUserRepository.findByAccount(account);

        //then
        assertThat(result).isPresent();
    }

    @DisplayName("유저 정보 생성시 이미 저장된 계정이 존재하면 예외가 발생한다.")
    @Test
    void createWithExistUser() {
        //given
        String account = "gugu";
        String password = "password";
        String email = "email@email.com";
        Map<String, String> params = Map.of(
                "account", account,
                "password", password,
                "email", email
        );

        //when //then
        assertThatThrownBy(() -> userService.create(params))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 등록된 계정으로 회원가입할 수 없습니다.");
    }
}
