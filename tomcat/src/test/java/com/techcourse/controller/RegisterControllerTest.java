package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.request.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

class RegisterControllerTest {

    @Nested
    @DisplayName("회원가입을 한다.")
    class MappingController {
        @Test
        @DisplayName("post register")
        void postRegister() {
            HttpRequest request = new HttpRequest(
                    new HttpRequestLine("POST /register HTTP/1.1"),
                    new HttpRequestHeader(
                            List.of("Host: localhost:8080", "Connection: keep-alive", "Content-Length: 80",
                                    "Content-Type: application/x-www-form-urlencoded", "Accept: */*")),
                    new HttpRequestBody("account=chorong&password=password&email=hkkang%40woowahan.com")
            );
            HttpResponse response = HttpResponse.defaultResponse();
            Controller controller = new RegisterController();
            controller.service(request, response);

            User user = InMemoryUserRepository.findByAccount("chorong").orElse(null);

            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(302);
            assertThat(response.getResponseHeader().get("Location")).isEqualTo("/index.html");
            assertThat(user.getAccount()).isEqualTo("chorong");
        }
    }
}
