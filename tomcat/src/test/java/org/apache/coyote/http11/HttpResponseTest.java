package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("응답 바디를 생성한다.")
    void generateResponseBody() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.generateResponse("test.html", Status.OK);

        assertThat(httpResponse.getResponse()).contains("test file\n");
    }

    @Test
    @DisplayName("확장자가 없다면 .html을 추가해, 응답 바디를 생성한다.")
    void generateResponseBodyWithoutExtension() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.generateResponse("test", Status.OK);

        assertThat(httpResponse.getResponse()).contains("test file\n");
    }

    @Test
    @DisplayName("200 응답을 생성한다.")
    void generate200Response() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.generateResponse("test.html", Status.OK);

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
    void generate302Response() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.generateResponse("test.html", Status.FOUND);

        String actual = "HTTP/1.1 302 FOUND \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Location: " + "test.html \r\n\r\n";
        String expected = httpResponse.getResponse();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("응답 헤더에 Set-Cookie를 추가한다.")
    void setCookie() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.generateResponse("test.html", Status.FOUND);
        httpResponse.addHeader("Set-Cookie", "JSESSIONID=abc");

        String actual = " \r\n"
                + "Set-Cookie: JSESSIONID=abc ";
        String expected = httpResponse.getResponse();

        assertThat(expected).contains(actual);
    }
}
