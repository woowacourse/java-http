package org.apache.coyote.web.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.support.HttpMethod;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void from() {
        RequestLine requestLine = RequestLine.from(new String[]{"GET", "/index.html", "HTTP/1.1"});

        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getRequestUrl()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.getQueryParameter()).isNull(),
                () -> assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    void fromHasQueryString() {
        RequestLine requestLine = RequestLine.from(new String[]{"GET", "/login?name=wogns", "HTTP/1.1"});

        assertThat(requestLine.getQueryParameter()).isEqualTo("name=wogns");
    }

    @Test
    void isSameMethod() {
        RequestLine requestLine = RequestLine.from(new String[]{"GET", "/index.html", "HTTP/1.1"});
        assertThat(requestLine.isSameMethod(HttpMethod.GET)).isTrue();
    }

    @Test
    void isSameUrl() {
        RequestLine requestLine = RequestLine.from(new String[]{"GET", "/login?name=wogns", "HTTP/1.1"});
        assertThat(requestLine.isSameUrl("/login")).isTrue();
    }

    @Test
    void getFileExtension() {
        RequestLine requestLine = RequestLine.from(new String[]{"GET", "/index.html", "HTTP/1.1"});

        assertThat(requestLine.getFileExtension().get()).isEqualTo("html");
    }

    @Test
    void getFileExtensionNotFile() {
        RequestLine requestLine = RequestLine.from(new String[]{"GET", "/login?name=wogns", "HTTP/1.1"});

        assertThat(requestLine.getFileExtension().isEmpty()).isTrue();
    }
}
