package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.*;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.TestRequest;

class HttpResponseTest {

    @Test
    @DisplayName("기본 HttpResponse의 생성 결과를 검증한다.")
    void httpResponse() {
        // when
        final HttpResponse response = new HttpResponse.Builder(TestRequest.generateWithUri("/path"))
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
        final HttpResponse response = new HttpResponse.Builder(TestRequest.generateWithUri("/path"))
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
        final HttpResponse response = new HttpResponse.Builder(TestRequest.generateWithUri("/path"))
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
        final HttpResponse response = new HttpResponse.Builder(TestRequest.generateWithUri("/path"))
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
}
