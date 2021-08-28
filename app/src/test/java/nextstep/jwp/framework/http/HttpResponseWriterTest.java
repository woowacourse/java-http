package nextstep.jwp.framework.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpResponseWriterTest {

    @DisplayName("OutputStream에 HttpResponse 작성 테스트")
    @Test
    void writeWithTest() throws IOException {

        // expected
        String expected = String.join("\r\n", "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                "");

        // given
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);
        HttpHeaders httpHeaders = new HttpHeaders().addHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
        HttpResponse httpResponse = new HttpResponse.Builder().statusLine(statusLine)
                                                              .httpHeaders(httpHeaders)
                                                              .contentLength(5564)
                                                              .build();

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        new HttpResponseWriter(httpResponse).writeWith(outputStream);

        // then
        assertThat(outputStream.toString()).isEqualTo(expected);
    }

    @DisplayName("HttpResponse 를 문자열로 변환 테스트")
    @Test
    void readFromResponseTest() {

        // expected
        String expected = String.join("\r\n", "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                "");

        // given
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);
        HttpHeaders httpHeaders = new HttpHeaders().addHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
        HttpResponse httpResponse = new HttpResponse.Builder().statusLine(statusLine)
                                                              .httpHeaders(httpHeaders)
                                                              .contentLength(5564)
                                                              .build();

        // when
        final String response = new HttpResponseWriter(httpResponse).readFromResponse();

        // then
        assertThat(response).isEqualTo(expected);
    }
}
