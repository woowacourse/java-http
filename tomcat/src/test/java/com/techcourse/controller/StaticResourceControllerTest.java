package com.techcourse.controller;

import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ResponseContent;
import org.apache.coyote.http11.ResponseContentResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("정적 리소스 컨트롤러")
class StaticResourceControllerTest {

    private final ResponseContentResolver resolver = mock(ResponseContentResolver.class);
    private final StaticResourceController controller = new StaticResourceController(resolver);

    @Test
    @DisplayName("요청 경로를 정적 리소스 조회에 사용한다")
    void resolvesStaticResourceByRequestPath() throws Exception {
        // given
        final var request = request("/index.html");
        final var content = new ResponseContent("text/html;charset=utf-8", new byte[0]);
        when(resolver.resolve("/index.html")).thenReturn(content);

        // when
        controller.service(request);

        // then
        verify(resolver).resolve("/index.html");
    }

    @Test
    @DisplayName("정적 리소스를 200 OK로 응답한다")
    void returnsOkForStaticResource() throws Exception {
        // given
        final var request = request("/index.html");
        final var content = new ResponseContent("text/html;charset=utf-8", new byte[0]);
        when(resolver.resolve("/index.html")).thenReturn(content);

        // when
        final var response = controller.service(request);

        // then
        assertThat(response.status()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("정적 리소스를 준비하지 못하면 예외의 상태로 응답한다")
    void returnsErrorStatusWhenResourceCannotBePrepared() throws Exception {
        // given
        final var request = request("/index.html");
        final var failure = new HttpException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "resource unavailable",
                new IOException("read failed"));
        when(resolver.resolve("/index.html")).thenThrow(failure);

        // when
        final var response = controller.service(request);

        // then
        assertThat(response.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpRequest request(final String path) throws Exception {
        final var rawRequest = String.join("\r\n",
                "GET " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        return HttpRequest.readFrom(new BufferedReader(new StringReader(rawRequest))).orElseThrow();
    }
}
