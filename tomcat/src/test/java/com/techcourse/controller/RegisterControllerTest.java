package com.techcourse.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.coyote.http.request.ContentType;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestLine;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @Test
    void 회원가입에_성공한다() throws IOException {
        HttpRequest request = registerRequest(
                "account=gyubin&password=password&email=gyubin%40woowahan.com");

        HttpResponse response = registerController.service(request);

        Assertions.assertThat(response.headers().get("Location"))
                .isPresent()
                .get(InstanceOfAssertFactories.STRING)
                .isEqualTo("/index.html");
    }

    private HttpRequest registerRequest(String body) {
        return new HttpRequest(
                RequestLine.from("POST /register HTTP/1.1 "),
                HttpHeaders.from(List.of(
                        "Content-Type: " + ContentType.FORM_URLENCODED.value(),
                        "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length)),
                RequestBody.of(ContentType.FORM_URLENCODED, body));
    }
}
