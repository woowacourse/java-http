package nextstep.jwp.controller;

import static ch.qos.logback.classic.Level.INFO;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExistUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class RegisterControllerTest {

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.rollback();
    }

    @DisplayName("회원가입을 성공하면 로그를 남긴다.")
    @Test
    void register() {
        // given
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        final Logger logger = (Logger) LoggerFactory.getLogger(RegisterController.class);
        logger.addAppender(appender);
        appender.start();

        final String account = "dwoo";
        final String password = "1234";
        final String email = "dwoo@email.com";
        final String requestBody = "account=" + account + "&password=" + password + "&email=" + email;
        final HttpRequest httpRequest = HttpRequest.of("POST /register HTTP/1.1",
                Map.of(), requestBody);

        // when
        final HttpResponse httpResponse = HttpResponse.from(new ByteArrayOutputStream());
        final RegisterController registerController = new RegisterController();
        registerController.service(httpRequest, httpResponse);

        // then
        final List<ILoggingEvent> logs = appender.list;
        final String message = logs.get(0).getFormattedMessage();
        final Level level = logs.get(0).getLevel();

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();

        assertThat(message).isEqualTo("회원가입 성공! : " + user);
        assertThat(level).isEqualTo(INFO);
    }

    @DisplayName("이미 존재하는 account로 회원가입시 ExistUserException이 발생한다.")
    @Test
    void failToLogin() {
        // given
        final String requestBody = "account=gugu&password=password&email=gugu@email.com";
        final HttpRequest httpRequest = HttpRequest.of("POST /register HTTP/1.1",
                Map.of(), requestBody);

        // when & then
        final HttpResponse httpResponse = HttpResponse.from(new ByteArrayOutputStream());
        final RegisterController registerController = new RegisterController();
        Assertions.assertThatThrownBy(() -> registerController.service(httpRequest, httpResponse))
                .isInstanceOf(ExistUserException.class);
    }
}
