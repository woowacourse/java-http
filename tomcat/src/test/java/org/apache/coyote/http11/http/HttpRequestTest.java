package org.apache.coyote.http11.http;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private final String request = String.join(System.lineSeparator(),
            "POST /cgi-bin/process.cgi HTTP/1.1 ",
            "Content-Type: application/x-www-form-urlencoded ",
            "Content-Length: 49 ",
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry",
            "",
            "licenseID=string&content=string&/paramsXML=string"
    );

    @Test
    void 요청을_파싱해서_Reqeust_Body를_생성() {
        // given, when
        try (final var inputStream = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8))) {
            final HttpRequest httpRequest = HttpRequest.from(inputStream);

            // then
            assertThat(httpRequest.getRequestBody()).isEqualTo("licenseID=string&content=string&/paramsXML=string");
        } catch (IOException ignore) {
        }
    }

    @Test
    void 세션정보를_가져온다() {
        // given, when
        try (final var inputStream = new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8))) {
            final HttpRequest httpRequest = HttpRequest.from(inputStream);

            // then
            assertAll(
                    () -> assertThat(httpRequest.getSession(true)).isInstanceOf(Session.class),
                    () -> assertThat(httpRequest.getSession(false)).isNull()
            );
        } catch (IOException ignore) {
        }
    }
}
