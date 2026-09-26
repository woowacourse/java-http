package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void writesRedirectResponseInHttpFormat() throws Exception {
        final HttpResponse response = new HttpResponse();
        response.sendRedirect("/index.html");
        response.addCookie("JSESSIONID", "session-id");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        response.write(outputStream);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .isEqualTo("HTTP/1.1 302 Found\r\n"
                        + "Location: /index.html\r\n"
                        + "Set-Cookie: JSESSIONID=session-id\r\n"
                        + "Content-Length: 0\r\n\r\n");
    }
}
