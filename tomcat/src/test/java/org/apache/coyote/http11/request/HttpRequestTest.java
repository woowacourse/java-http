package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.coyote.http11.Headers;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void getSession_jsessionid가_있는_경우_기존의_session을_반환한다() {
        RequestLine requestLine = RequestLine.of("GET /login http/1.1");
        Headers headers = Headers.of(List.of("Cookie: JSESSIONID=jsessionid"));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, "");

        assertThat(httpRequest.getSession(false)).isNotNull();
    }

    @Test
    void getSession_jsessionid가_없는_경우_새로운_session을_반환한다() {
        RequestLine requestLine = RequestLine.of("GET /login http/1.1");
        Headers headers = Headers.of(List.of(""));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, "");

        assertThat(httpRequest.getSession(false)).isNotNull();
    }

    @Test
    void getSession_create가_true인_경우_새로운_session을_반환한다() {
        RequestLine requestLine = RequestLine.of("GET /login http/1.1");
        Headers headers = Headers.of(List.of(""));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, "");

        assertThat(httpRequest.getSession(true)).isNotNull();
    }

    @Test
    void getMethod() {
        RequestLine requestLine = RequestLine.of("GET /login http/1.1");
        Headers headers = Headers.of(List.of(""));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, "");

        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    void getRequestParameters() {
        RequestLine requestLine = RequestLine.of("GET /login?account=account&password=password http/1.1");
        Headers headers = Headers.of(List.of(""));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, "");

        assertAll(() -> {
                    assertThat(httpRequest.getRequestParameters().get("account")).isEqualTo("account");
                    assertThat(httpRequest.getRequestParameters().get("password")).isEqualTo("password");
                }
        );
    }
}
