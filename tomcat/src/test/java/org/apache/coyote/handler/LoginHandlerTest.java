package org.apache.coyote.handler;

import static ch.qos.logback.classic.Level.INFO;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class LoginHandlerTest {

    @DisplayName("존재하는 회원의 account 로 로그인시 로그인을 성공하여 로그를 남긴다.")
    @Test
    void login() {
        // given
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        final Logger logger = (Logger) LoggerFactory.getLogger(LoginHandler.class);
        logger.addAppender(appender);
        appender.start();

        final String account = "gugu";
        String requestUrl = "login?account=" + account + "&password=password";

        // when
        LoginHandler.login(requestUrl);

        // then
        final List<ILoggingEvent> logs = appender.list;
        final String message = logs.get(0).getFormattedMessage();
        final Level level = logs.get(0).getLevel();

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();

        assertThat(message).isEqualTo(user.toString());
        assertThat(level).isEqualTo(INFO);
    }
}
