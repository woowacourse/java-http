package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceHandlerTest {

    @Test
    @DisplayName("static 하위에 존재하는 리소스라면 정적 리소스 핸들러가 처리할 수 있다.")
    void canHandle() {
        StaticResourceHandler staticResourceHandler = new StaticResourceHandler();

        boolean result = staticResourceHandler.canHandle(createHttpRequest("GET /sample.txt HTTP/1.1"));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("static 하위에 존재하지 않는 리소스라면 정적 리소스 핸들러가 처리할 수 없다.")
    void cantHandle() {
        StaticResourceHandler staticResourceHandler = new StaticResourceHandler();

        boolean result = staticResourceHandler.canHandle(createHttpRequest("GET /unknown.txt HTTP/1.1"));

        assertThat(result).isFalse();
    }

    private HttpRequest createHttpRequest(String startLine) {
        return new HttpRequest(startLine, new Header(Collections.emptyList()), new QueryParameter(""));
    }
}
