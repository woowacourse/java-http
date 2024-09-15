package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import com.techcourse.servlet.DispatcherServlet;
import com.techcourse.servlet.RequestMapping;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.ServletContainer;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class IndexControllerTest {

    private final RequestMapping requestMapping = new RequestMapping(Map.of(
            "/", new IndexController()
    ));
    private final List<Servlet> servlet = List.of(new DispatcherServlet(requestMapping));
    private final ServletContainer servletContainer = ServletContainer.init(servlet);

    @Test
    @DisplayName("GET '/' 요청에 대한 응답이 정상적으로 처리된다.")
    void hello() {
        // given
        StubSocket socket = new StubSocket();
        Http11Processor processor = new Http11Processor(servletContainer, socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }
}
