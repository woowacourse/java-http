package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.util.HttpRequestParser;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpRequestParserTest {

    @Test
    void HTTP요청을_받아서_HttpRequest_객체를_생성한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login?name=gugu&password=1234 HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 50",
                "Accept: text/html",
                "",
                "account=gugu&email=gugu@mail.com&password=password");

        // when
        HttpRequest parsed = HttpRequestParser.parse(
                new BufferedReader(
                        new InputStreamReader(
                                new ByteArrayInputStream(httpRequest.getBytes())
                        )
                )
        );

        // then
        assertAll(
                () -> assertThat(parsed.getUrl().getUrlPath()).isEqualTo("/login"),
                () -> assertThat(parsed.getUrl().getQueryStrings().getQueryString("name")).isEqualTo("gugu"),
                () -> assertThat(parsed.getUrl().getQueryStrings().getQueryString("password")).isEqualTo("1234"),
                () -> assertThat(parsed.getHeaders().getHeaderValues(HttpHeader.ACCEPT)).isEqualTo(
                        List.of("text/html")),
                () -> assertThat(parsed.getBody().getValue("account")).isEqualTo("gugu"),
                () -> assertThat(parsed.getBody().getValue("email")).isEqualTo("gugu@mail.com"),
                () -> assertThat(parsed.getBody().getValue("password")).isEqualTo("password")
        );
    }
}

