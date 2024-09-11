package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.db.InMemoryUserRepository;

class RegisterControllerTest {

    private final Controller registerController = RegisterController.getInstance();

    @DisplayName("회원 가입 요청이 오면 회원을 저장하고 /index.html로 리다이렉트한다.")
    @Test
    void register() throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu2&password=password&email=hkkang%40woowahan.com ");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        registerController.service(request, response);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/index.html \r\n" +
                "\r\n";
        assertAll(
                () -> assertThat(outputStream.toString()).isEqualTo(expected),
                () -> assertThat(InMemoryUserRepository.findByAccount("gugu2")).isPresent()
        );
        InMemoryUserRepository.deleteByAccount("gugu2");
        inputStream.close();
        outputStream.close();
    }
}
