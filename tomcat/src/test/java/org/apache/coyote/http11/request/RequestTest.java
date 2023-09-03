package org.apache.coyote.http11.request;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    @DisplayName("Request를 생성한다.")
    void convert() throws IOException {
        //given
        final String input = "GET /index.html HTTP/1.1\r\n"
                + "key1: value1\r\n"
                + "key2: value2\r\n"
                + "";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));

        //when
        final Request request = Request.convert(bufferedReader);

        //then
        Map<String, String> expectedHeader = new HashMap<>();
        expectedHeader.put("key1", "value1");
        expectedHeader.put("key2", "value2");

        assertSoftly((softly) -> {
            softly.assertThat(request.getHeader()).isEqualTo(expectedHeader);
            softly.assertThat(request.getLine().getHttpMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(request.getLine().getRequestPath()).isEqualTo("/index.html");
            softly.assertThat(request.getLine().getHttpVersion()).isEqualTo("HTTP/1.1");
        });
    }

}
