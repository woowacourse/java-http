package nextstep.jwp.infrastructure.http.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.infrastructure.http.Headers;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.Method;
import nextstep.jwp.infrastructure.http.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestReaderTest {

    @DisplayName("HttpRequest 파싱 테스트")
    @Test
    void readHttpRequest() throws IOException {
        final String request = String.join("\r\n",
            "GET /notFound HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        final HttpRequestReader reader = new HttpRequestReader(getBufferedReader(request));

        final HttpRequest expected = new HttpRequest(
            new RequestLine(Method.GET, "/notFound"),
            new Headers.Builder()
                .header("Host", "localhost:8080")
                .header("Connection", "keep-alive")
                .build(),
            ""
        );

        assertThat(reader.readHttpRequest()).isEqualTo(expected);
    }

    @DisplayName("잘못된 HttpRequest 형태일 경우 예외처리")
    @Test
    void readInvalidHttpRequest() throws IOException {
        final String request = String.join("\r\n",
            "GET /notFound  HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        final HttpRequestReader reader = new HttpRequestReader(getBufferedReader(request));

        assertThatIllegalArgumentException().isThrownBy(reader::readHttpRequest);
    }

    private BufferedReader getBufferedReader(final String value) {
        final InputStream inputStream = new ByteArrayInputStream(value.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
    }
}