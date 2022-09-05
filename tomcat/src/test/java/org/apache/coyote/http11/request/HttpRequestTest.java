package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private static final HttpRequestBody EMPTY_REQUEST_BODY = new HttpRequestBody("");
    private static final HttpRequestHeader EMPTY_REQUEST_HEADER = new HttpRequestHeader(List.of());

    @DisplayName(value = "같은 HttpMethod인 경우 isSameHttpMethod는 true")
    @Test
    void isSameHttpMethod() {
        // given
        final HttpRequest httpRequest = new HttpRequest("GET /login HTTP/1.1 ", EMPTY_REQUEST_HEADER,
                EMPTY_REQUEST_BODY);

        // when & then
        assertThat(httpRequest.isGetMethod()).isTrue();
    }
}
