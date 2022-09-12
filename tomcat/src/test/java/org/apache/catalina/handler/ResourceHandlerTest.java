package org.apache.catalina.handler;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.TestRequest;

public class ResourceHandlerTest {

    @Test
    @DisplayName("RequestUri에 해당하는 파일을 읽어온다.")
    void render() {
        // given
        final HttpRequest request = TestRequest.generateWithRequestLine("GET", "/test.txt", "HTTP/1.1");

        // when
        final HttpResponse response = ResourceHandler.render(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 9 ",
            "",
            "test file");

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }

    @Test
    @DisplayName("RequestUri에 해당하는 파일을 읽어온다. 확장자가 없을 경우 .html을 붙여 가져온다.")
    void render_withoutExtension() {
        // given
        final HttpRequest request = TestRequest.generateWithRequestLine("GET", "/404", "HTTP/1.1");

        // when
        final HttpResponse response = ResourceHandler.render(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 13 ",
            "",
            "test 404.html");

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }
}
