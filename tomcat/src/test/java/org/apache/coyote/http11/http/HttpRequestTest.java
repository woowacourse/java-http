package org.apache.coyote.http11.http;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 요청을_파싱해서_Reqeust_Body를_생성() {
        // given
        final var request = String.join(System.lineSeparator(),
                "POST /cgi-bin/process.cgi HTTP/1.1 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: 49 ",
                "",
                "licenseID=string&content=string&/paramsXML=string"
        );
        try (final var inputStream = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8))) {
            // when
            final HttpRequest httpRequest = HttpRequest.from(inputStream);

            // then
            assertThat(httpRequest.getRequestBody()).isEqualTo("licenseID=string&content=string&/paramsXML=string");
        } catch (IOException ignore) {
        }
    }
}
