package org.apache.coyote.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RootEndPointHandlerTest {

    @Test
    @DisplayName("루트 엔드포인트에 대한 요청 처리: 모든 요청에 대해 동일한 응답 반환")
    void handle_OtherHttpMethods() {
        final String[] httpMethods = {"PUT", "DELETE", "PATCH", "OPTIONS"};

        for (String method : httpMethods) {
            final HttpRequest request = new HttpRequest(method, "/", "HTTP/1.1", null, null);

            final String result = RootEndPointHandler.getInstance().handle(request);

            assertThat(result).contains("Hello world!");
        }
    }

}
