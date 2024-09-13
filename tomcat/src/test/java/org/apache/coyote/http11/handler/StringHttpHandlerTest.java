package org.apache.coyote.http11.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpUrl;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringHttpHandlerTest {

    @Test
    @DisplayName("StringHttpHandler는 요청에 대한 응답으로 정적 문자열을 반환한다.")
    void handleTest() throws IOException {
        // given
        StringHttpHandler stringHttpHandler = new StringHttpHandler("Hello, World!");
        HttpRequest request = new HttpRequest(HttpMethod.GET, new HttpUrl("/"));

        // when
        HttpResponse response = stringHttpHandler.handle(request);

        // then
        assertEquals("Hello, World!", new String(response.getBody()));
    }
}
