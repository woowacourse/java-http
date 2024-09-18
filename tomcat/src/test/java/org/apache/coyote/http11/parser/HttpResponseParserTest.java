package org.apache.coyote.http11.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.data.HttpCookie;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseParserTest {

    @Test
    @DisplayName("문자열 HTTP 응답을 생성한다.")
    void parse() {
        // given
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1).setHttpStatusCode(HttpStatusCode.OK)
                .setResponseBody("Hello Word!")
                .addCookie(new HttpCookie("KEY", "VALUE", Map.of("Max-Age", "600")));

        // when
        String parsedResponse = HttpResponseParser.parse(httpResponse);

        // then
        assertThat(parsedResponse).isEqualTo(
                "HTTP/1.1 200 OK \r\nContent-Length: 11 \r\nSet-Cookie: KEY=VALUE; Max-Age=600\r\n\r\nHello Word!");
    }
}
