package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.handler.IndexHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class IndexHandlerTest {

    @Test
    @DisplayName("루트 경로 요청을 처리할 수 있다.")
    void canHandle() {
        IndexHandler indexHandler = new IndexHandler();

        boolean result = indexHandler.canHandle(createHttpRequest("GET / HTTP/1.1"));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("루트 경로가 아닌 요청은 처리할 수 없다.")
    void cantHandle() {
        IndexHandler indexHandler = new IndexHandler();

        boolean result = indexHandler.canHandle(createHttpRequest("GET /login HTTP/1.1"));

        assertThat(result).isFalse();
    }

    private HttpRequest createHttpRequest(String startLine) {
        return new HttpRequest(startLine, new Header(Collections.emptyList()));
    }
}
