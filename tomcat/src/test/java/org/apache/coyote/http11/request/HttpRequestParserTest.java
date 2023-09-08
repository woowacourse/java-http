package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestParserTest {

    private final HttpRequestParser httpRequestParser = new HttpRequestParser();

    @Test
    void Http_요청을_입력받아_HttpRequest를_반환한다() throws IOException {
        // given
        final String content = "account=gugu&password=password&email=hkkang@woowahan.com";
        final String input = String.join(
                CRLF,
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + content.getBytes().length,
                "",
                content
        );
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));

        // when
        final HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);

        // then
        final RequestLine requestLine = httpRequest.getRequestLine();
        final Headers headers = httpRequest.getHeaders();
        final RequestBody requestBody = httpRequest.getRequestBody();
        assertAll(
                () -> assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(requestLine.parseUri()).isEqualTo("/register"),
                () -> assertThat(requestLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1),
                () -> assertThat(headers.getItems()).contains(
                        entry("Host", "localhost:8080"),
                        entry("Content-Length", String.valueOf(content.getBytes().length))
                ),
                () -> assertThat(requestBody.getItems()).contains(
                        entry("account", "gugu"),
                        entry("password", "password"),
                        entry("email", "hkkang@woowahan.com")
                )
        );
    }
}
