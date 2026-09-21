package org.apache.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseParserTest {

    private final HttpResponseParser parser = new HttpResponseParser();

    @Test
    void 상태_라인과_헤더와_본문을_HTTP_응답으로_변환한다() {
        // given
        String body = "로그인 성공";
        int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

        HttpTomcatResponse response = HttpTomcatResponse.createDefault();
        response.setStatus(302);
        response.setBody(body);
        response.setLocation("/index.html");
        response.setCookie("JSESSIONID=session-id");

        // when
        String result = parser.parse(response);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + contentLength + " ",
                "Location: /index.html ",
                "Set-Cookie: JSESSIONID=session-id ",
                "",
                body
        );

        assertThat(result).isEqualTo(expected);
    }
}
