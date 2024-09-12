package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestTest {

    @DisplayName("객체 생성 시 문자열을 HTTP 요청 형식에 맞게 파싱하여 필드에 저장한다.")
    @Test
    void should_parseComponents_when_construct() throws IOException {
        // given
        String input = "POST /path HTTP/1.1\r\n"
                + "headerKey: headerValue\r\n"
                + "\r\n"
                + "body\r\n";
        BufferedReader reader = new BufferedReader(new StringReader(input));

        // when
        Request request = new Request(reader);

        // then
        assertThat(request.hasPath("/path")).isTrue();
        assertThat(request.hasMethod(RequestMethod.POST)).isTrue();
        assertThat(request.getBody()).isInstanceOf(RequestBody.class);
    }
}
