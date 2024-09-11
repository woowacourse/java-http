package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.coyote.http11.request.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("body가 없는 경우 \r\n\r\n으로 끝나는 response를 작성한다.")
    @Test
    void withoutBody() throws IOException {
        // given
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);
        response.addStatusLine("HTTP/1.1 200 OK");
        response.addHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
        response.addHeader(HttpHeader.CONTENT_LENGTH, "12");

        // when
        response.writeResponse();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "");
        assertThat(outputStream.toString()).isEqualTo(expected);
        outputStream.close();
    }

    @DisplayName("body가 있는 경우 헤더와 body 사이에 \r\n\r\n가 존재하고 \r\n으로 끝나는 response를 작성한다.")
    @Test
    void witBody() throws IOException {
        // given
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);
        response.addStatusLine("HTTP/1.1 200 OK");
        response.addHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
        response.addHeader(HttpHeader.CONTENT_LENGTH, "12");
        response.addBody("Hello world!");

        // when
        response.writeResponse();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!",
                "");
        assertThat(outputStream.toString()).isEqualTo(expected);
        outputStream.close();
    }
}
