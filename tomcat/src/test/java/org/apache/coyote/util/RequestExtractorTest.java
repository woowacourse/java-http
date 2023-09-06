package org.apache.coyote.util;

import static org.apache.coyote.header.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.header.HttpMethod;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RequestExtractorTest {

    @Test
    void 요청의_HTTP_Method를_추출한다() {
        // given
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Content-Type: text/html;charset=utf-8",
                "",
                "");

        // when
        HttpMethod result = RequestExtractor.extractHttpMethod(request);

        // then
        assertThat(result).isEqualTo(GET);
    }

    @Test
    void 요청의_목표_경로를_추출한다() {
        // given
        String request = String.join("\r\n",
                "GET /login?account=gugu&password=1234 HTTP/1.1 ",
                "Content-Type: text/html;charset=utf-8",
                "",
                "");

        // when
        String result = RequestExtractor.extractTargetPath(request);

        // then
        assertThat(result).isEqualTo("/login?account=gugu&password=1234");
    }

    @Test
    void 쿼리_파라미터를_추출한다() {
        // given
        String request = String.join("\r\n",
                "GET /login?account=gugu&password=1234 HTTP/1.1 ",
                "Accept: text/html",
                "",
                "");

        // when
        Map<String, String> result = RequestExtractor.extractQueryParam(request);

        // then
        assertThat(result.get("account")).isEqualTo("gugu");
        assertThat(result.get("password")).isEqualTo("1234");
    }
}
