package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.route.DefaultDispatcher;
import org.apache.catalina.route.RequestMapper;
import org.apache.catalina.route.RequestMapping;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class ErrorControllerTest {

    @DisplayName("요청 처리 중 해결되지 않은 예외가 던져지면 500 에러 페이지를 응답한다.")
    @Test
    void successNotFoundGetTest() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "", ""
        );

        // when
        StubSocket socket = new StubSocket(httpRequest);
        RequestMapping requestMapping = new RequestMapper();
        requestMapping.register(new AbstractController() {
            @Override
            protected void doGet(HttpRequest request, HttpResponse response) {
                throw new RuntimeException();
            }

            @Override
            protected void doPost(HttpRequest request, HttpResponse response) {
                throw new RuntimeException();
            }

            @Override
            public String matchedPath() {
                return "/";
            }
        });
        DefaultDispatcher dispatcher = new DefaultDispatcher(requestMapping);
        dispatcher.setUnresolvedErrorHandler(ErrorController.class);
        Http11Processor processor = new Http11Processor(socket, dispatcher);
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/500.html");
        String content = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        String contentLength = Integer.toString(content.getBytes(StandardCharsets.UTF_8).length);
        String expected = String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error",
                "Content-Length: " + contentLength,
                "Content-Type: text/html;charset=utf-8",
                "",
                content);

        assertThat(socket.output()).isEqualTo(expected);
    }

}
