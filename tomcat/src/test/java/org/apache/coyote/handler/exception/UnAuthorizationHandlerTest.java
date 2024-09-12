package org.apache.coyote.handler.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnAuthorizationHandlerTest {

    @Test
    @DisplayName("모든 요청 처리: 401 응답 반환")
    void handle() {
        final RequestLine requestLine = new RequestLine("GET", "/index.html", "HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine, null, null);

        final HttpResponse httpResponse = HttpResponse.builder().unauthorizedBuild();
        assertThat(UnAuthorizationHandler.getInstance().handle(httpRequest)).isEqualTo(httpResponse);
    }
}
