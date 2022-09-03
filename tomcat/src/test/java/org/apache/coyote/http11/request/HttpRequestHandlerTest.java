package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpMethod;
import org.apache.coyote.http11.request.model.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHandlerTest {

    @Test
    @DisplayName("http 요청의 첫 줄을 분리하여 HttpRequest를 생성한다.")
    void of() {
        String startLine = "GET /index.html HTTP/1.1";

        HttpRequest httpRequest = HttpRequestHandler.newHttpRequest(startLine);

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getUri().getValue()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getVersion()).isEqualTo(HttpVersion.HTTP_1_1)
        );
    }
}
