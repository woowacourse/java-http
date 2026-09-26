package org.apache.catalina.controller;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.requestline.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AbstractControllerTest {
    private final SessionManager sessionManager = new SessionManager();

    private final Controller getOnly = new AbstractController() {
        @Override
        protected void doGet(final HttpRequest request, final HttpResponse response) {
            response.setBody("got".getBytes());
        }
    };

    @ParameterizedTest
    @ValueSource(strings = {"GET", "HEAD"})
    void GET과_HEAD는_doGet으로_보낸다(final String method) throws Exception {
        final HttpResponse response = new HttpResponse();

        getOnly.service(request(method), response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("got".getBytes());
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "DELETE"})
    void 구현하지_않은_메서드는_405와_Allow를_응답한다(final String method) throws Exception {
        final HttpResponse response = new HttpResponse();

        getOnly.service(request(method), response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getHeader(HttpHeaderName.ALLOW)).hasValue("GET, HEAD");
    }

    @Test
    void 재정의한_메서드만_Allow에_포함한다() throws Exception {
        final Controller getAndPost = new AbstractController() {
            @Override
            protected void doGet(final HttpRequest request, final HttpResponse response) {
            }

            @Override
            protected void doPost(final HttpRequest request, final HttpResponse response) {
            }
        };
        final HttpResponse response = new HttpResponse();

        getAndPost.service(request("PUT"), response);

        assertThat(response.getHeader(HttpHeaderName.ALLOW)).hasValue("GET, HEAD, POST");
    }

    private HttpRequest request(final String method) {
        return HttpRequest.of(
                RequestLine.from(method + " / HTTP/1.1"),
                RequestHeaders.from(List.of("Host: localhost")),
                RequestBody.empty(),
                sessionManager
        );
    }
}