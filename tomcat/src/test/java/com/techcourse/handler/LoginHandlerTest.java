package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.RequestBody;
import org.apache.coyote.http.RequestLine;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class LoginUserHandlerTest {

    private final LoginUserHandler loginHandler = new LoginUserHandler();

    @Test
    void 로그인에_성공한_경우_쿠키를_설정한다() {
        HttpServletRequest request = loginRequest("account=gugu&password=password");

        HttpServletResponse response = loginHandler.handle(request);

        assertThat(response.headers().get("Set-Cookie"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .startsWith("JSESSIONID=");
    }

    @Test
    void 로그인에_실패한_경우_401_페이지로_이동한다() {
        HttpServletRequest request = loginRequest("account=gugu&password=wrongPassword");

        HttpServletResponse response = loginHandler.handle(request);

        assertThat(response.headers().get("Location"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .isEqualTo("/401.html");
    }

    private HttpServletRequest loginRequest(String body) {
        return new HttpServletRequest(
                RequestLine.from("POST /login HTTP/1.1 "),
                HttpHeaders.from(List.of("Content-Type: " + ContentType.FORM_URLENCODED.value())),
                RequestBody.of(ContentType.FORM_URLENCODED, body));
    }
}
