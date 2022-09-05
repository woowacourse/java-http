package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import nextstep.jwp.model.User;
import nextstep.jwp.repository.InMemoryUserRepository;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.LoggerFactory;

@DisplayName("UserService 클래스의")
class UserServiceTest {

    private String createRequestString(final String query) {
        return String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Content-Length: " + query.length() + " ",
                "Connection: keep-alive ",
                "",
                query);
    }

    @Nested
    @DisplayName("login 메서드는")
    class Login {

        @Test
        @DisplayName("계정 정보가 존재하면 해당 유저 정보를 로그에 출력한다.")
        void success() {
            // given
            final String requestAsString = createRequestString("account=gugu&password=password");
            final InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            final Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);
            final ListAppender<ILoggingEvent> memoryAppender = new ListAppender<>();
            logger.addAppender(memoryAppender);
            memoryAppender.start();

            // when
            final UserService userService = new UserService();
            userService.login(httpRequest);

            // then
            assertAll(
                    () -> assertThat(memoryAppender.list).hasSize(1),
                    () -> assertThat(memoryAppender.list.get(0).getLevel()).isEqualTo(Level.INFO),
                    () -> assertThat(memoryAppender.list.get(0).getFormattedMessage()).isEqualTo(
                            "user : User{id=1, account='gugu', email='hkkang@woowahan.com', password='password'}")
            );
        }

        @ParameterizedTest
        @CsvSource({"account=pepper&password=pwd", "account=gugu&password=pwd"})
        @DisplayName("계정 정보가 존재하지 않는 경우 예외를 던진다.")
        void invalidAccount_ExceptionThrown(final String query) {
            // given
            final String requestAsString = createRequestString(query);
            final InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final UserService userService = new UserService();

            // when & then
            assertThatThrownBy(() -> userService.login(httpRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("유효하지 않은 유저입니다.");
        }
    }

    @Nested
    @DisplayName("register 메서드는")
    class Register {

        @Test
        @DisplayName("유저 정보를 저장한다.")
        void success() {
            // given
            final String requestAsString = createRequestString(
                    "account=pepper&password=password&email=pepper%40woowahan.com");
            final InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            // when
            final UserService userService = new UserService();
            userService.register(httpRequest);

            // then
            final Optional<User> pepper = InMemoryUserRepository.findByAccount("pepper");

            assertThat(pepper).isPresent();
            assertThat(pepper.get().getAccount()).isEqualTo("pepper");
        }
    }
}
