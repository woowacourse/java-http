package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11InputStreamReaderTest {

    @Test
    @DisplayName("Content-Length 에 맞는 body 를 읽는다.")
    void read() throws IOException {
        // given
        String body = """
                {"account:"seyang", "password": "pw", "email": "se@yang.com"}
                """;
        int requestBodyLength = body.getBytes().length;
        int contentLength = body.getBytes().length;
        byte[] request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + contentLength,
                "",
                body
        ).getBytes();
        InputStream inputStream = new ByteArrayInputStream(request);

        // when
        List<String> data = Http11InputStreamReader.read(inputStream);
        int readBodyLength = data.getLast().getBytes().length;

        // then
        assertAll(
                () -> assertThat(readBodyLength).isEqualTo(requestBodyLength),
                () -> assertThat(readBodyLength).isEqualTo(contentLength)
        );
    }

    @Test
    @DisplayName("실제 body 길이가 Content-Length 보다 클 경우 Content-Length 만큼만 읽는다.")
    void readAsContentLength() throws IOException {
        // given
        String body = """
                {"account:"seyang", "password": "pw", "email": "se@yang.com"}
                """;
        int requestBodyLength = body.getBytes().length;
        int contentLength = requestBodyLength - 10;
        byte[] request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + (contentLength),
                "",
                body
        ).getBytes();
        InputStream inputStream = new ByteArrayInputStream(request);

        // when
        List<String> data = Http11InputStreamReader.read(inputStream);
        int readBodyLength = data.getLast().getBytes().length;

        // then
        assertAll(
                () -> assertThat(readBodyLength).isLessThan(requestBodyLength),
                () -> assertThat(readBodyLength).isEqualTo(contentLength)
        );
    }

    @Test
    @DisplayName("실제 body 길이가 Content-Length 보다 작을 경우 Content-Length 만큼만 읽는다.")
    void readAsLengthOfBody() throws IOException {
        // given
        String body = """
                {"account:"seyang", "password": "pw", "email": "se@yang.com"}
                """;
        int requestBodyLength = body.getBytes().length;
        int contentLength = requestBodyLength + 10;
        byte[] request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + (contentLength),
                "",
                body
        ).getBytes();
        InputStream inputStream = new ByteArrayInputStream(request);

        // when
        List<String> data = Http11InputStreamReader.read(inputStream);
        int readBodyLength = data.getLast().getBytes().length;

        // then
        assertAll(
                () -> assertThat(readBodyLength).isEqualTo(requestBodyLength),
                () -> assertThat(readBodyLength).isLessThan(contentLength)
        );
    }
}
