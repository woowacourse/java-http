package org.apache.coyote.http11.request;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.request.requestline.RequestLine;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final RequestBody emptyBody = RequestBody.empty();

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

    @Test
    void 쿼리와_본문의_파라미터를_출처별로_조회한다() {
        final HttpRequest request = HttpRequest.of(
                RequestLine.from("POST /login?source=query HTTP/1.1"),
                RequestHeaders.from(List.of("Host: localhost", "Content-Type: application/x-www-form-urlencoded")),
                RequestBody.of("account=gugu".getBytes(StandardCharsets.US_ASCII),
                        Optional.of("application/x-www-form-urlencoded")),
                sessionManager
        );

        assertThat(request.getQueryParameter("source")).hasValue("query");
        assertThat(request.getBodyParameter("account")).hasValue("gugu");
        assertThat(request.getBodyParameter("source")).isEmpty();
        assertThat(request.getQueryParameter("account")).isEmpty();
    }
}