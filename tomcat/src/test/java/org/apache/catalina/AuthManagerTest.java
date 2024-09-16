package org.apache.catalina;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.Method;
import org.apache.coyote.http11.RequestLine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthManagerTest {

    @DisplayName("인증된 유저가 아니면 예외를 던진다.")
    @Test
    void not_authenticate() {
        // given
        RequestLine requestLine = new RequestLine(Method.POST, "/login", "HTTP/1.1");
        HttpHeaders httpHeaders = new HttpHeaders();
        String requestBody = "account=zeze&password=1234";

        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders, requestBody);

        // when & then
        Assertions.assertThatCode(() -> AuthManager.authenticate(httpRequest))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("인증된 유저가 아닙니다.");
    }

    @DisplayName("유저를 인증 후 인증정보를 전달한다.")
    @Test
    void authenticate() {
        // given
        InMemoryUserRepository.save(new User("zeze", "1234", "zeze@gmail.com"));
        RequestLine requestLine = new RequestLine(Method.POST, "/login", "HTTP/1.1");
        HttpHeaders httpHeaders = new HttpHeaders();
        String requestBody = "account=zeze&password=1234";

        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders, requestBody);

        // when & then
        Assertions.assertThatCode(() -> AuthManager.authenticate(httpRequest))
                .doesNotThrowAnyException();

    }

}
