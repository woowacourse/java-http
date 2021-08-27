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
    @DisplayName("inputStream을 바탕으로 Request를 읽어낸다.")
    void read() throws IOException {
        // given
        String expected = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ");
        InputStream inputStream = new ByteArrayInputStream(expected.getBytes());

        // when
        HttpRequestStreamReader reader = new HttpRequestStreamReader(inputStream);
        List<String> result = reader.read();
        String actual = String.join("\r\n", result);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Request에 body가 존재할 때에, Content-Length를 바탕으로 body 데이터를 포함해서 읽어낸다.")
    void read_formData() throws IOException {
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
        List<String> result = reader.read();
        String actual = String.join("\r\n", result);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}