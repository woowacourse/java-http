package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void parsesGetRequestWithoutQuery() throws IOException {
        HttpRequest request = parse("GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.isGetMethod()).isTrue();
        assertThat(request.getRequestTarget()).isEqualTo("/index.html");
        assertThat(request.getQueryParameters()).isEmpty();
    }

    @Test
    void preservesPostMethod() throws IOException {
        HttpRequest request = parse("POST /register HTTP/1.1\r\n\r\n");

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.isGetMethod()).isFalse();
        assertThat(request.isPath("/register")).isTrue();
    }

    @Test
    void matchesOnlyTheExactPath() throws IOException {
        HttpRequest request = parse("GET /login HTTP/1.1\r\n\r\n");

        assertThat(request.isPath("/login")).isTrue();
        assertThat(request.isPath("/log")).isFalse();
        assertThat(request.isPath("/login.html")).isFalse();
        assertThat(request.isPath("/LOGIN")).isFalse();
    }

    @Test
    void separatesLoginPathFromQueryParameters() throws IOException {
        HttpRequest request = parse("GET /login?account=gugu&password=password HTTP/1.1\r\n\r\n");

        assertThat(request.getRequestTarget()).isEqualTo("/login");
        assertThat(request.isPath("/login")).isTrue();
        assertThat(request.getQueryParameters()).containsExactlyInAnyOrderEntriesOf(Map.of("account", "gugu", "password", "password"));
    }

    @Test
    void parsesParametersRegardlessOfTheirOrder() throws IOException {
        HttpRequest request = parse("GET /login?password=password&account=gugu HTTP/1.1\r\n\r\n");

        assertThat(request.getQueryParameters()).containsExactlyInAnyOrderEntriesOf(Map.of("account", "gugu", "password", "password"));
    }

    @Test
    void preservesAnEmptyParameterValue() throws IOException {
        HttpRequest request = parse("GET /login?account=gugu&password= HTTP/1.1\r\n\r\n");

        assertThat(request.getQueryParameters()).containsExactlyInAnyOrderEntriesOf(Map.of("account", "gugu", "password", ""));
    }

    @Test
    void preservesEqualsSignsInsideAValue() throws IOException {
        HttpRequest request = parse("GET /login?password=a=b=c HTTP/1.1\r\n\r\n");

        assertThat(request.getQueryParameters()).containsEntry("password", "a=b=c");
    }

    @Test
    void preservesQuestionMarksInsideAValue() throws IOException {
        HttpRequest request = parse("GET /login?password=a?b HTTP/1.1\r\n\r\n");

        assertThat(request.getQueryParameters()).containsEntry("password", "a?b");
    }

    @Test
    void rejectsABlankRequestLine() {
        assertThatThrownBy(() -> parse("\r\n")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void propagatesAnInputFailure() {
        IOException failure = new IOException("Input failed");
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw failure;
            }
        };

        assertThatThrownBy(() -> HttpRequest.parse(inputStream)).isSameAs(failure);
    }

    private HttpRequest parse(String rawRequest) throws IOException {
        return HttpRequest.parse(new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8)));
    }
}
