package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("사용자 로그인이 정상적으로 처리된다.")
    @Test
    void userLogin() throws IOException {
        InMemoryUserRepository.save(new User("green", "1234", "green@0wooteco.com&"));

        final LoginController loginController = new LoginController();

        final String requestLine = "POST /login.html HTTP/1.1";
        final HttpHeader httpHeader = new HttpHeader(String.join("\r\n",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: keep-alive "));
        final HttpBody httpBody = new HttpBody("account=green&email=green@0wooteco.com&password=1234");

        final HttpRequest httpRequest = new HttpRequest(requestLine, httpHeader, httpBody);

        final HttpResponse httpResponse = loginController.service(httpRequest,
                new HttpResponse());

        assertThat(httpResponse.getResponse()).contains("302 Found");
    }
}
