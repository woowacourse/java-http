package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ContentTypeTest {

    @ParameterizedTest
    @CsvSource(value = {
        "text/css:text/css;charset=utf-8",
        "text/html:text/html;charset=utf-8",
        "application/json:application/json;charset=utf-8",
        "default:text/html;charset=utf-8"
    }, delimiter = ':')
    @DisplayName("HttpRequest을 받아 반환할 ContentType을 추출한다.")
    void from(String accept, String expected) {
        // given
        HttpRequest request = generateRequest(accept);

        // when
        String actual = ContentType.from(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private HttpRequest generateRequest(final String accept) {
        final String acceptHeader = "Accept: " + accept;
        final String httpRequest = String.join("\r\n",
            "GET /path HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 39",
            "Content-Type: application/x-www-form-urlencoded",
            acceptHeader,
            "",
            "name=sojukang&email=kangsburg@gmail.com");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        try {
            return new HttpRequest(inputStream);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("Invalid byte requested");
        }
    }
}
