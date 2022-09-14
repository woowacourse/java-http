package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void isStaticFileRequest() {
        String protocol = "HTTP/1.1";
        URI uri = new URI("/login.html");
        RequestLine requestLine = new RequestLine(Method.GET, uri, protocol);

        assertThat(requestLine.isStaticFileRequest()).isTrue();
    }

    @Test
    void getExtension() {
        String protocol = "HTTP/1.1";
        URI uri = new URI("/login.html");
        RequestLine requestLine = new RequestLine(Method.GET, uri, protocol);
        Optional<String> extension = requestLine.getExtension();

        assertAll(
                () -> assertThat(extension).isPresent(),
                () -> assertThat(extension.get()).isEqualTo("html")
        );
    }
}
