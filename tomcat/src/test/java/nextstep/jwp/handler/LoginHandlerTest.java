package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.QueryParams;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class LoginHandlerTest {

    @Test
    @DisplayName("회원이 존재하고 비밀번호가 일치하면 로그인을 성공한다. - 로그 확인")
    void login_success() {
        // given
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        Logger logger = (Logger) LoggerFactory.getLogger(LoginHandler.class);
        logger.addAppender(listAppender);
        listAppender.start();
        List<ILoggingEvent> testLogs = listAppender.list;

        QueryParams queryParams = QueryParams.from("account=leo&password=password");
        User user = new User(1L, queryParams.get("account"), queryParams.get("password"), "leo@woowahan.com");
        InMemoryUserRepository.save(user);

        // when
        LoginHandler.login(queryParams);
        String actual = testLogs.get(0).getFormattedMessage();

        // then
        String expected = "User{id=1, account='leo', email='leo@woowahan.com', password='password'}";
        assertThat(actual).isEqualTo(expected);
    }
}
