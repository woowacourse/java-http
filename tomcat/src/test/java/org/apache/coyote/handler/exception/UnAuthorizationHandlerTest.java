package org.apache.coyote.handler.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponseGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnAuthorizationHandlerTest {

    @Test
    @DisplayName("모든 요청 처리: 401 응답 반환")
    void handle() {
        HttpRequest httpRequest = new HttpRequest("GET", null, null, null, null);
        assertThat(UnAuthorizationHandler.getInstance().handle(httpRequest)).isEqualTo(HttpResponseGenerator.getUnauthorizedResponse());
    }
}
