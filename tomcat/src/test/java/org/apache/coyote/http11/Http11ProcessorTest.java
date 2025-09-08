package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.ServletContainer;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {


    @Test
    void processor_요청_응답_흐름_검증() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                ""
        );

        StubSocket socket = new StubSocket(httpRequest);

        // Servlet 목 생성
        Servlet mockServlet = mock(Servlet.class);

        // ServletContainer에 등록
        ServletContainer container = ServletContainer.getInstance();
        container.add("/index.html", mockServlet);

        Http11Processor processor = new Http11Processor(socket, container);

        // when
        processor.process(socket);

        // then
        // service 호출 여부 확인
        verify(mockServlet).service(any(), any());

        // HttpResponse의 기본 응답값이 200이므로 이것이 나오는지 검증
        String output = socket.output();
        assertThat(output).startsWith("HTTP/1.1 200 OK");
    }
}
