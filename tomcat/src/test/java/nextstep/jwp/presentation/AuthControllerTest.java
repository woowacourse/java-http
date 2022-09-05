package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.exception.ElementNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthControllerTest {

    @DisplayName("잘못된 param으로 UserRequest를 생성하여 예외가 발생한다.")
    @Test
    void queryParamNotFoundException() {
        final AuthController authController = new AuthController();
        final String startLine = "GET /login?account1=gu&password=password HTTP/1.1";
        final HttpHeader httpHeader = new HttpHeader(startLine,
                String.join("\r\n",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: 12 ",
                        ""));
        final HttpBody httpBody = new HttpBody("");

        assertThatThrownBy(() ->
                authController.run(httpHeader, httpBody))
                .hasMessageContaining( "존재하지 않는 데이터입니다.")
                .isInstanceOf(ElementNotFoundException.class);
    }

    @DisplayName("사용자 회원가입이 정상적으로 처리된다..")
    @Test
    void userRegister() {
        final AuthController authController = new AuthController();

        final String startLine = "POST /register HTTP/1.1";
        final HttpHeader httpHeader = new HttpHeader(startLine,
                String.join("\r\n",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: keep-alive "));
        final HttpBody httpBody = new HttpBody("account=green&email=green@0wooteco.com&password=1234");

       authController.run(httpHeader, httpBody);

        final User user = InMemoryUserRepository.findByAccount("green").get();

        assertThat(user.getAccount()).isEqualTo("green");
        assertThat(user.checkPassword("1234")).isTrue();
    }
}
