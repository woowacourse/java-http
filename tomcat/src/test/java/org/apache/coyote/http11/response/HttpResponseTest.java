package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @Test
    @DisplayName("기본 HttpResponse의 생성 결과를 검증한다.")
    void httpResponse() {
        // when
        final HttpResponse response = new HttpResponse.Builder(generateRequest())
            .build();

        final String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 0 ",
            "", ""
        );

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }

    @Test
    @DisplayName("Redirect 했을 경우 HttpResponse의 생성 결과를 검증한다.")
    void httpResponse_redirect() {
        // when
        final HttpResponse response = new HttpResponse.Builder(generateRequest())
            .redirect()
            .build();

        final String expected = String.join("\r\n",
            "HTTP/1.1 302 FOUND ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 0 ",
            "", ""
        );

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }

    @Test
    @DisplayName("MessageBody가 있을 경우 경우 HttpResponse의 생성 결과를 검증한다.")
    void httpResponse_messageBody() {
        // when
        Resource resource = new Resource("test");
        final HttpResponse response = new HttpResponse.Builder(generateRequest())
            .messageBody(resource)
            .build();

        final String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 4 ",
            "",
            "test"
        );

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }

    @Test
    @DisplayName("Cookie가 있을 경우 경우 HttpResponse의 생성 결과를 검증한다.")
    void httpResponse_cookie() {
        // when
        final Session session = new Session("testId");
        final HttpCookie cookie = HttpCookie.fromJSession(session);
        final HttpResponse response = new HttpResponse.Builder(generateRequest())
            .cookie(cookie)
            .build();

        final String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 0 ",
            "Set-Cookie: JSESSIONID=testId ",
            "", ""
        );

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }

    private HttpRequest generateRequest() {
        final String httpRequest = String.join("\r\n",
            "GET /path HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 39",
            "Content-Type: application/x-www-form-urlencoded",
            "",
            "name=sojukang&email=kangsburg@gmail.com");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        try {
            return new HttpRequest(inputStream);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("Invalid byte requested");
        }
    }
}
