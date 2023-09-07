package org.apache.coyote.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpResponseTest {

    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        httpResponse = new HttpResponse();
    }

    @Test
    void updateStartLine() {
        //given
        String expect = "HTTP/1.1 200 OK";

        //when
        httpResponse.updateStartLine(expect);

        //then
        assertThat(httpResponse.getStartLine()).isEqualTo(expect);

    }

    @Test
    void updateMessageBody() {
        //given
        String expect = "Hello World";

        //when
        httpResponse.updateMessageBody(expect);

        //then
        assertThat(httpResponse.getMessageBody()).isEqualTo(expect);
    }

    @Test
    void updateFileMessageBody() throws IOException {
        //given
        int expect = 5564;

        //when
        httpResponse.updateFileMessageBody("/index.html");

        //then
        assertThat(httpResponse.getMessageBody().getBytes().length).isEqualTo(expect);
    }

    @Test
    void addHeader() {
        //given
        String key = "Content-Type";
        String value = "text/html; charset=utf-8";

        //when
        httpResponse.addHeader(key, value);

        //then
        assertThat(httpResponse.getHeader().get(key)).isEqualTo(value);
    }

    @Test
    void addCookie() {
        //given
        String key = "a";
        String value = "b";

        //when
        httpResponse.addCookie(key, value);

        //then
        assertThat(httpResponse.getHeader().get("Cookie")).isEqualTo(key + "=" + value);
    }

    @Test
    void joinResponse() {
        //given
        String expect = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: 4 \r\n" +
                "\r\n" +
                "test";

        //when
        httpResponse.updateStartLine("HTTP/1.1 200 OK \r\n");
        httpResponse.updateMessageBody("test");
        httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(httpResponse.getMessageBody().getBytes().length));

        //then
        String actual = httpResponse.joinResponse();
        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 200 OK"),
                () -> assertThat(actual).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(actual).contains("Content-Length: 4"),
                () -> assertThat(actual).contains("test")
        );
    }
}