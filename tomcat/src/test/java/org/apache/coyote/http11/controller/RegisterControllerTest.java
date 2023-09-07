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

class RegisterControllerTest {

    @BeforeEach
    void beforeEach() {
        SessionManager.clear();
    }

    @Test
    @DisplayName("회원가입을 완료하면 index.html로 리다이렉트한다.")
    void register() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "",
                "account=newnew&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);


        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final List<String> expected = List.of(
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(output).contains(expected);
        });
    }

}
