package nextstep.jwp.http.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import nextstep.jwp.http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpParserTest {

    @DisplayName("HTTP 요청 InputStream을 파싱하여 HttpRequest를 생성한다. (바디 미포함)")
    @Test
    void parse_바디를_포함하고_있지_않는_경우() {
        // given
        String httpRequestMessage = String.join("\r\n",
            "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Accept: */*",
                ""
            );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());

        // when
        HttpRequest actual = HttpParser.parse(inputStream);

        // then
        assertThat(actual.asString()).isEqualTo(httpRequestMessage + "\r\n");
    }

    @DisplayName("HTTP 요청 InputStream을 파싱하여 HttpRequest를 생성한다. (바디 포함)")
    @Test
    void parse_바디를_포함하고_있는_경우() {
        // given
        String httpRequestMessage = String.join("\r\n",
            "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 58",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());

        // when
        HttpRequest actual = HttpParser.parse(inputStream);

        // then
        assertThat(actual.asString()).isEqualTo(httpRequestMessage);
    }
}
