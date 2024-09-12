package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import java.util.List;
import org.apache.coyote.http11.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestTest {

    @DisplayName("세션이 존재하는지 여부 정상 판단 (true)")
    @Test
    void hasSession_True() {
        // given
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        HttpHeader httpHeader = new HttpHeader(List.of("Cookie: JSESSIONID=sessionid"));
        RequestBody requestBody = new RequestBody("");
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeader, requestBody);

        // when & then
        assertThat(httpRequest.hasSession()).isTrue();
    }

    @DisplayName("세션이 존재하는지 여부 정상 판단 (false)")
    @ParameterizedTest
    @ValueSource(strings = {"Not-Cookie: JSESSIONID=sessionid", "Cookie: JSESSIONI=sessionid", "Cookie: JSESSIONID="})
    void hasSession_False(String rawCookie) {
        // given
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        HttpHeader httpHeader = new HttpHeader(List.of(rawCookie));
        RequestBody requestBody = new RequestBody("");
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeader, requestBody);

        // when & then
        assertThat(httpRequest.hasSession()).isFalse();
    }

    @DisplayName("세션을 가져온다.")
    @Test
    void getSession_Success() {
        // given
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        HttpHeader httpHeader = new HttpHeader(List.of("Cookie: JSESSIONID=sessionid"));
        RequestBody requestBody = new RequestBody("");
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeader, requestBody);

        // when & then
        assertThat(httpRequest.getSession()).isEqualTo("sessionid");
    }

    @DisplayName("세션이 없는 경우 세션을 가져오는 과정에서 예외 발생")
    @ParameterizedTest
    @ValueSource(strings = {"Not-Cookie: JSESSIONID=sessionid", "Cookie: JSESSIONI=sessionid", "Cookie: JSESSIONID="})
    void getSession_Fail(String rawHeader) {
        // given
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        HttpHeader httpHeader = new HttpHeader(List.of(rawHeader));
        RequestBody requestBody = new RequestBody("");
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeader, requestBody);

        // when & then
        assertThatThrownBy(httpRequest::getSession)
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("세션이 존재하지 않습니다.");
    }

    @DisplayName("HTTP 메서드가 무엇인지를 판단한다.")
    @Test
    void hasMethod() {
        // given
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        HttpHeader httpHeader = new HttpHeader(List.of("Cookie: JSESSIONID=sessionid"));
        RequestBody requestBody = new RequestBody("");
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeader, requestBody);

        // when & then
        assertAll(
                () -> assertThat(httpRequest.hasMethod(HttpMethod.GET)).isTrue(),
                () -> assertThat(httpRequest.hasMethod(HttpMethod.POST)).isFalse()
        );
    }
}
