package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.RequestBody;
import org.apache.coyote.http.RequestLine;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    LoginHandler loginHandler = new LoginHandler();


    @Test
    void 로그인에_성공한_경우_쿠키를_설정한다() throws IOException {
        HttpServletRequest request = new HttpServletRequest(
                RequestLine.from("GET /login?account=gugu&password=password HTTP/1.1 "),
                HttpHeaders.from(List.of()),
                RequestBody.of("")
        );

        HttpServletResponse response = loginHandler.handle(request);

        assertThat(response.headers().get("Set-Cookie"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .startsWith("JSESSIONID=");
    }

    @Test
    void 로그인에_실패한_경우_404_페이지로_이동한다() throws IOException {
        HttpServletRequest request = new HttpServletRequest(
                RequestLine.from("GET /login?account=gugu&password=wrongPassword HTTP/1.1 "),
                HttpHeaders.from(List.of()),
                RequestBody.of("")
        );

        HttpServletResponse response = loginHandler.handle(request);

        assertThat(response.headers().get("Location"))
                .get(InstanceOfAssertFactories.STRING)
                .isEqualTo("/401.html");
    }
}
