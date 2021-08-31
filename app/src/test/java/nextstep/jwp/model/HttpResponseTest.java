package nextstep.jwp.model;

import nextstep.jwp.HttpServlet;
import nextstep.jwp.MockSocket;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.util.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @DisplayName("인덱스 페이지 조회에 대한 응답을 확인한다.")
    @Test
    void responseForward() throws IOException {
        // given
        MockSocket socket = new MockSocket("GET /index HTTP/1.1 \r\n");
        HttpServlet httpServlet = new HttpServlet(socket);

        // when
        httpServlet.run();

        // then
        String body = FileUtils.readFileOfUrl("/index.html");
        String expectedHeader = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
//                "Content-Length: 5564 ",
                "Content-Length: 5670 ",
                "",
                body);
        assertThat(socket.output()).hasToString(expectedHeader);
    }

    @DisplayName("401 페이지 리다이렉트에 대한 응답을 확인한다.")
    @Test
    void responseRedirect() throws IOException {
        // given
        String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 32",
                "",
                "account=gugu&password=password11");
        MockSocket socket = new MockSocket(request);
        HttpServlet httpServlet = new HttpServlet(socket);

        // when
        httpServlet.run();

        // then
        String expectedHeader = String.join("\r\n",
                "HTTP/1.1 302 Redirect ",
                "Location: /401.html ",
                "");
        assertThat(socket.output()).hasToString(expectedHeader);
    }
}