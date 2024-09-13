package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("응답 바디를 생성한다.")
    void getResponseBody() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        String responseBody = httpResponse.generateResponseBody("test.html");

        assertThat(responseBody).isEqualTo("test file\n");
    }

    @Test
    @DisplayName("확장자가 없다면 .html을 추가해, 응답 바디를 생성한다.")
    void getResponseBodyWithoutExtension() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        String responseBody = httpResponse.generateResponseBody("test");

        assertThat(responseBody).isEqualTo("test file\n");
    }

    @Test
    @DisplayName("200 응답을 생성한다.")
    void generate200Response() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        String responseBody = httpResponse.generateResponseBody("test.html");
        httpResponse.generate200Response("test.html", responseBody);

        String actual = "HTTP/1.1 200 OK \r\n"
                + "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: 10 \r\n"
                + "\r\n"
                + "test file\n";
        String expected = httpResponse.getResponse();
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    @DisplayName("302 응답을 생성한다.")
    void generate302Response() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.generate302Response("test.html");

        String actual = "HTTP/1.1 302 FOUND \r\n" +
                "Location: " + "test.html \r\n" +
                "Content-Type: text/html;charset=utf-8 ";
        String expected = httpResponse.getResponse();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("응답 헤더에 Set-Cookie를 추가한다.")
    void sass() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.appendSetCookieHeader("abc");

        String actual = " \r\n"
                + "Set-Cookie: JSESSIONID=abc ";
        String expected = httpResponse.getResponse();

        assertThat(actual).isEqualTo(expected);

    }
}
