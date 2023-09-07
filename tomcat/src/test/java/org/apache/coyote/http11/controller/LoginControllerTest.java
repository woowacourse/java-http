package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.common.SessionManager;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

class LoginControllerTest {

    @BeforeEach
    void beforeEach() {
        SessionManager.clear();
    }

    @Test
    @DisplayName("로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 " +
            "/index.html로 리다이렉트 하고 응답 헤더에 Set-Cookie를 추가하고 " +
            "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 형태로 값을 전달하고" +
            "Session 객체의 값으로 User 객체를 저장한다.")
    void successLogin() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);


        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final List<String> expected = List.of(
                "Set-Cookie: JSESSIONID=",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(output).contains(expected);
            softAssertions.assertThat(SessionManager.size()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("로그인에 실패하면 401.html로 리다이렉트한다.")
    void failLogin() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "",
                "account=failfail&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);


        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final List<String> expected = List.of(
                "Content-Type: text/html;charset=utf-8 ",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(output).contains(expected);
            softAssertions.assertThat(SessionManager.size()).isEqualTo(0);
        });
    }
}
