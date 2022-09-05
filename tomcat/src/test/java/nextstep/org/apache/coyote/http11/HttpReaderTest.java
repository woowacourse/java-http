package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.coyote.http11.HttpReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpReaderTest {

    @Test
    @DisplayName("BufferReader를 통해서 첫번째 라인을 읽어온다.")
    void getStartLine() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */*",
                "");
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpReader httpReader = new HttpReader(bufferedReader);

        // when
        final String startLine = httpReader.getStartLine();

        // then
        assertThat(startLine).isEqualTo("GET /login?account=gugu&password=password HTTP/1.1 ");
    }

    @Test
    @DisplayName("BufferReader를 통해서 헤더부분들을 읽어온다.")
    void getHeaders() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "Accept: */*"
        );
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpReader httpReader = new HttpReader(bufferedReader);

        // when
        final List<String> headers = httpReader.getHeaders();

        // then
        assertThat(headers).containsExactly(
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "Accept: */*"
        );
    }
}
