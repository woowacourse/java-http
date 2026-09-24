package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.request.Cookies;
import org.apache.coyote.http11.request.FormContents;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    @Test
    @DisplayName("회원가입에 성공하면 사용자를 저장하고 메인 페이지로 리다이렉트한다.")
    void redirectToIndexWhenRegisterSucceeds() throws Exception {
        // given
        HttpRequest request = postRegisterRequest("account=javajigi&password=password&email=javajigi%40example.com");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        new RegisterController().service(request, new HttpResponse(outputStream));

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
        assertThat(InMemoryUserRepository.findByAccount("javajigi"))
                .isPresent();
    }

    @Test
    @DisplayName("회원가입 필수 값이 비어 있으면 저장하지 않고 401 페이지로 리다이렉트한다.")
    void redirectToUnauthorizedWhenRequiredValueIsBlank() throws Exception {
        // given
        HttpRequest request = postRegisterRequest("account=&password=&email=");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        new RegisterController().service(request, new HttpResponse(outputStream));

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /401.html");
        assertThat(InMemoryUserRepository.findByAccount(""))
                .isEmpty();
    }

    private HttpRequest postRegisterRequest(String body) {
        return new HttpRequest(
                new RequestLine("POST /register HTTP/1.1"),
                HttpHeaders.empty(),
                FormContents.from(body),
                Cookies.from(null)
        );
    }
}
