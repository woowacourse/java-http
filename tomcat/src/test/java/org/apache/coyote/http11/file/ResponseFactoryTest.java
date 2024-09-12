package org.apache.coyote.http11.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ResponseFactoryTest {
    private static final String HTTP_LINE_SEPARATOR = "\r\n";

    @DisplayName("HttpResponse를 만들 수 있다.")
    @Test
    void testWriteResponse() throws IOException {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusLine("200", "OK");
        httpResponse.setFieldValue("Content-Length", "5506");
        httpResponse.setFieldValue("Content-Type", "text/html;charset=utf-8");
        httpResponse.setBody("Sample Response Body");

        // when
        ResponseFactory.writeResponse(outputStream, httpResponse);
        String actual = outputStream.toString();

        // then
        String expected = String.join(HTTP_LINE_SEPARATOR,
                "HTTP/1.1 200 OK",
                "Content-Length: 5506",
                "Content-Type: text/html;charset=utf-8",
                "",
                "Sample Response Body");
        assertThat(actual).isEqualTo(expected);
    }
}
