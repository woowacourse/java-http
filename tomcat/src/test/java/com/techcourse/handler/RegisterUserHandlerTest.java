package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.db.InMemoryUserRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.RequestBody;
import org.apache.coyote.http.RequestLine;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class RegisterUserHandlerTest {

    RegisterUserHandler registerUserHandler = new RegisterUserHandler();

    @Test
    void 회원가입에_성공한다() {
        HttpServletRequest request = registerRequest(
                "account=gyubin&password=password&email=gyubin%40woowahan.com");

        HttpServletResponse response = registerUserHandler.handle(request);

        Assertions.assertThat(response.headers().get("Location"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .isEqualTo("/index.html");
    }

    private HttpServletRequest registerRequest(String body) {
        return new HttpServletRequest(
                RequestLine.from("POST /register HTTP/1.1 "),
                HttpHeaders.from(List.of(
                        "Content-Type: application/x-www-form-urlencoded",
                        "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length)),
                RequestBody.of("application/x-www-form-urlencoded", body));
    }
}
