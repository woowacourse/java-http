package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestParserTest {

    @Test
    void HTTP_요청을_파싱한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequestParser requestParser = HttpRequestParser.from(reader);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestParser)
                    .isNotNull();

            softly.assertThat(requestParser.getHttpRequestFirstLineInfo())
                    .isNotNull()
                    .extracting("httpMethod", "uri", "versionOfTheProtocol")
                    .containsExactly(HttpMethod.GET, "/index.html", "HTTP/1.1");

            softly.assertThat(requestParser.getHeaders())
                    .isNotNull()
                    .hasSize(4)
                    .containsEntry("Host", "localhost:8080 ")
                    .containsEntry("Connection", "keep-alive ")
                    .containsEntry("Content-Length", "80")
                    .containsEntry("Content-Type", "application/x-www-form-urlencoded");

            softly.assertThat(requestParser.getBody())
                    .isNotNull()
                    .hasSize(3)
                    .containsEntry("account", "gugu")
                    .containsEntry("password", "password");
        });
    }

    @Test
    void 요청에_헤더가_없으면_예외를_발생시킨다() {
        // given
        final String httpRequest = "";

        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // expect
        assertThatThrownBy(() -> HttpRequestParser.from(reader))
                .isInstanceOf(IOException.class)
                .hasMessage("요청에 헤더가 존재하지 않습니다.");
    }

    @Test
    void 요청에_바디_중_잘못된_값이_있으면_예외를_발생시킨다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 84",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu=god&password=password&email=hkkang%40woowahan.com");

        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // expect
        assertThatThrownBy(() -> HttpRequestParser.from(reader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청의 바디가 잘못되었습니다.");
    }
}
