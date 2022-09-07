package nextstep.org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.ACCEPT;
import static org.apache.coyote.http11.HttpHeader.CONNECTION;
import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpHeader.HOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpReaderTest {

    @Test
    @DisplayName("BufferReader를 통해서 읽어온 요청이 빈 값인 경우 예외가 발생한다.")
    void emptyRequest() {
        // given
        final String request = "";
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when, then
        assertThatThrownBy(() -> new HttpReader(bufferedReader))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

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
                "Content-Length: 58 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com"
        );
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpReader httpReader = new HttpReader(bufferedReader);

        // when
        final HttpHeaders httpHeaders = httpReader.getHttpHeaders();

        // then
        assertAll(
                () -> assertThat(httpHeaders.getValue(HOST)).isEqualTo("localhost:8080"),
                () -> assertThat(httpHeaders.getValue(CONNECTION)).isEqualTo("keep-alive"),
                () -> assertThat(httpHeaders.getValue(CONTENT_LENGTH)).isEqualTo("58"),
                () -> assertThat(httpHeaders.getValue(CONTENT_TYPE)).isEqualTo("application/x-www-form-urlencoded"),
                () -> assertThat(httpHeaders.getValue(ACCEPT)).isEqualTo("*/*")
        );
    }

    @Test
    @DisplayName("BufferReader를 통해서 바디부분을 읽어온다.")
    void getBody() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 58 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com"
        );
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpReader httpReader = new HttpReader(bufferedReader);

        System.out.println("account=gugu&password=password&email=hkkang%40woowahan.com".getBytes().length);
        // when
        final String body = httpReader.getBody();

        // then
        assertThat(body).isEqualTo("account=gugu&password=password&email=hkkang%40woowahan.com");
    }
}
