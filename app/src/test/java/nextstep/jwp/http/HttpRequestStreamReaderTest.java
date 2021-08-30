package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestStreamReaderTest {

    @Test
    @DisplayName("getRequestLines()를 통해 inputStream을 바탕으로 Request를 읽어낸다.")
    void getRequestLines() throws IOException {
        // given
        String expected = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ");
        InputStream inputStream = new ByteArrayInputStream(expected.getBytes());

        // when
        HttpRequestStreamReader reader = new HttpRequestStreamReader(inputStream);
        List<String> result = reader.getRequestLines();
        String actual = String.join("\r\n", result);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("getRequestLines()를 통해 Request에 body가 존재할 때에, Content-Length를 바탕으로 body 데이터를 포함해서 읽어낸다.")
    void getRequestLines_formData() throws IOException {
        // given
        String formData = "account=gugu&password=password";
        String expected = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + formData.length(),
            "",
            "account=gugu&password=password");
        InputStream inputStream = new ByteArrayInputStream(expected.getBytes());

        // when
        HttpRequestStreamReader reader = new HttpRequestStreamReader(inputStream);
        List<String> result = reader.getRequestLines();
        String actual = String.join("\r\n", result);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("getStatusLine()를 통해 Request에서 StatusLine만 조회한다.")
    void getStatusLine() throws IOException {
        // given
        String expected = "POST /login HTTP/1.1 ";
        String s = String.join("\r\n",
            expected,
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "account=gugu&password=password");
        InputStream inputStream = new ByteArrayInputStream(s.getBytes());

        // when
        HttpRequestStreamReader reader = new HttpRequestStreamReader(inputStream);
        String actual = reader.getStatusLine();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("getHeaderLines()를 통해 Request에서 HeaderLine만 조회한다.")
    void getHeaderLines() throws IOException {
        // given
        String header1 = "Host: localhost:8080 ";
        String header2 = "Connection: keep-alive ";
        String s = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            header1,
            header2,
            "",
            "account=gugu&password=password");
        InputStream inputStream = new ByteArrayInputStream(s.getBytes());

        // when
        HttpRequestStreamReader reader = new HttpRequestStreamReader(inputStream);
        List<String> actual = reader.getHeaderLines();

        // then
        assertThat(actual).hasSize(2)
            .contains(header1)
            .contains(header2);
    }

    @Test
    @DisplayName("body()를 통해 Request에서 Body만 조회한다.")
    void getBody() throws IOException {
        // given
        String body = "account=gugu&password=password";
        String s = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + body.length(),
            "",
            body);
        InputStream inputStream = new ByteArrayInputStream(s.getBytes());

        // when
        HttpRequestStreamReader reader = new HttpRequestStreamReader(inputStream);
        String actual = reader.getBodyLine();

        // then
        assertThat(actual).isEqualTo(body);
    }
}