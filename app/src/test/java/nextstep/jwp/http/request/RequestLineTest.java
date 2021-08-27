package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("request line")
class RequestLineTest {

    @Test
    void BufferedReader를_사용하여_line를_읽어_객체를_생성한다() {
        final String requestLineAsString = "GET /index.html HTTP/1.1";
        final HttpMethod expectedMethod = HttpMethod.GET;
        final String expectedUri = "/index.html";

        final InputStream inputStream = new ByteArrayInputStream(requestLineAsString.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final RequestLine actualLine = new RequestLine(bufferedReader);

        assertAll(
                () -> assertThat(actualLine.getMethod()).isEqualTo(expectedMethod),
                () -> assertThat(actualLine.getUri()).isEqualTo(expectedUri)
        );
    }
}