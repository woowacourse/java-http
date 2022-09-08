package org.apache.coyote.web.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.support.HttpMethod;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {

    @Test
    void from() {
        HttpRequestLine httpRequestLine = HttpRequestLine.from(new String[]{"GET", "/index.html", "HTTP/1.1"});

        assertAll(
                () -> assertThat(httpRequestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequestLine.getRequestUrl()).isEqualTo("/index.html"),
                () -> assertThat(httpRequestLine.getQueryParameter()).isNull(),
                () -> assertThat(httpRequestLine.getVersion()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    void fromHasQueryString() {
        HttpRequestLine httpRequestLine = HttpRequestLine.from(new String[]{"GET", "/login?name=wogns", "HTTP/1.1"});

        assertThat(httpRequestLine.getQueryParameter()).isEqualTo("name=wogns");
    }

    @Test
    void isSameMethod() {
        HttpRequestLine httpRequestLine = HttpRequestLine.from(new String[]{"GET", "/index.html", "HTTP/1.1"});
        assertThat(httpRequestLine.isSameMethod(HttpMethod.GET)).isTrue();
    }

    @Test
    void isSameUrl() {
        HttpRequestLine httpRequestLine = HttpRequestLine.from(new String[]{"GET", "/login?name=wogns", "HTTP/1.1"});
        assertThat(httpRequestLine.isSameUrl("/login")).isTrue();
    }

    @Test
    void getFileExtension() {
        HttpRequestLine httpRequestLine = HttpRequestLine.from(new String[]{"GET", "/index.html", "HTTP/1.1"});

        assertThat(httpRequestLine.getFileExtension().get()).isEqualTo("html");
    }

    @Test
    void getFileExtensionNotFile() {
        HttpRequestLine httpRequestLine = HttpRequestLine.from(new String[]{"GET", "/login?name=wogns", "HTTP/1.1"});

        assertThat(httpRequestLine.getFileExtension().isEmpty()).isTrue();
    }
}
