package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import java.io.BufferedReader;
import java.io.StringReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    void post_register_success() throws Exception {
        // given
        final String body = "account=pobi&password=1234&email=pobi@test.com";

        final String httpRequest = String.join(
                "\r\n",
                "POST /register HTTP/1.1",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        final HttpRequest request = new HttpRequest(
                new BufferedReader(
                        new StringReader(httpRequest)
                )
        );

        final HttpResponse response = new HttpResponse();
        final RegisterController controller = new RegisterController();

        // when
        controller.service(request, response);

        // then
        assertThat(InMemoryUserRepository.findByAccount("pobi"))
                .isPresent();
        assertThat(response.toResponse())
                .contains("HTTP/1.1 302 Found")
                .contains("location: /index.html");
    }
}