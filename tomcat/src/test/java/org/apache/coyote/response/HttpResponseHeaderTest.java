package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpMessageBodyInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseHeaderTest {

    private HttpResponseHeader httpResponseHeader;

    @BeforeEach
    void setUp() {
        httpResponseHeader = new HttpResponseHeader();
    }

    @DisplayName("컨텐츠 타입을 추가한다.")
    @Test
    void addTextContentType() {
        httpResponseHeader.add(HttpMessageBodyInfo.CONTENT_TYPE.getValue(), "text/html");

        String expected = "Content-Type: text/html;charset=utf-8 ";

        assertThat(expected).isEqualTo(httpResponseHeader.toString());
    }

    @DisplayName("컨텐츠 길이를 추가한다.")
    @Test
    void addContentLength() {
        httpResponseHeader.add(HttpMessageBodyInfo.CONTENT_LENGTH.getValue(), "10");

        String expected = "Content-Length: 10 ";

        assertThat(expected).isEqualTo(httpResponseHeader.toString());
    }

    @DisplayName("쿠키를 추가한다.")
    @Test
    void addCookie() {
        httpResponseHeader.addCookie("key", "abc123");

        String expected = "Set-Cookie: key=abc123 ";

        assertThat(expected).isEqualTo(httpResponseHeader.toString());
    }

    @DisplayName("Location을 추가한다.")
    @Test
    void addLocation() {
        httpResponseHeader.addLocation("/index.html");

        String expected = "Location: /index.html ";

        assertThat(expected).isEqualTo(httpResponseHeader.toString());
    }

    @DisplayName("헤더에 여러 값을 추가 한다.")
    @Test
    void addMultipleValue() {
        httpResponseHeader.add(HttpMessageBodyInfo.CONTENT_TYPE.getValue(), "application/json");
        httpResponseHeader.add(HttpMessageBodyInfo.CONTENT_LENGTH.getValue(), "100");
        httpResponseHeader.addCookie("name", "kaki");
        httpResponseHeader.addLocation("/index.html");

        String expected = String.join("\r\n",
                "Content-Type: application/json ",
                "Content-Length: 100 ",
                "Set-Cookie: name=kaki ",
                "Location: /index.html "
        );

        assertThat(expected).isEqualTo(httpResponseHeader.toString());
    }

    @Test
    void addDuplicateValue() {
        httpResponseHeader.addCookie("name", "kaki");
        httpResponseHeader.addCookie("name", "gugu");

        String expected = "Set-Cookie: name=kaki; name=gugu ";

        assertThat(expected).isEqualTo(httpResponseHeader.toString());
    }
}
