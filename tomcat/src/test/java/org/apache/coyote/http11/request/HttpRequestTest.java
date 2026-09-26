package org.apache.coyote.http11.request;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.request.requestline.RequestLine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final RequestBody emptyBody = RequestBody.from("");

    @Test
    void HTTP_1_1_요청에_Host가_없으면_거부한다() {
        final RequestLine line = RequestLine.from("GET / HTTP/1.1");
        final RequestHeaders headers = RequestHeaders.from(List.of());

        assertThatThrownBy(() -> HttpRequest.of(line, headers, emptyBody, sessionManager))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void HTTP_1_0_요청은_Host가_없어도_된다() {
        final RequestLine line = RequestLine.from("GET / HTTP/1.0");
        final RequestHeaders headers = RequestHeaders.from(List.of());

        assertThatCode(() -> HttpRequest.of(line, headers, emptyBody, sessionManager))
                .doesNotThrowAnyException();
    }

    @Test
    void HTTP_1_1_요청에_Host가_있으면_통과한다() {
        final RequestLine line = RequestLine.from("GET / HTTP/1.1");
        final RequestHeaders headers = RequestHeaders.from(List.of("Host: localhost"));

        assertThatCode(() -> HttpRequest.of(line, headers, emptyBody, sessionManager))
                .doesNotThrowAnyException();
    }
}