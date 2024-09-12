package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 메서드를_반환() {
        // given
        RequestLine requestLine = new RequestLine(HttpMethod.GET.name(), null, null, null);
        HttpRequest request = new HttpRequest(requestLine, null, null);

        // when
        HttpMethod actual = request.getMethod();

        // then
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 콘텐츠_타입을_반환() {
        // given
        String contentType = "text/html";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", contentType);
        RequestHeader requestHeader = new RequestHeader(headers);
        HttpRequest request = new HttpRequest(null, requestHeader, null);

        // when
        String actual = request.getContentType();

        // then
        assertThat(actual).isEqualTo(contentType);
    }

    @Test
    void 콘텐츠_타입이_없으면_null을_반환() {
        // given
        String contentType = null;
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", contentType);
        RequestHeader requestHeader = new RequestHeader(headers);
        HttpRequest request = new HttpRequest(null, requestHeader, null);

        // when
        String actual = request.getContentType();

        // then
        assertThat(actual).isEqualTo(null);
    }

    @Test
    void 쿠키를_반환() {
        // given
        String attribute = "JSESSIONID=";
        String jSessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", attribute + jSessionId);
        RequestHeader requestHeader = new RequestHeader(headers);
        HttpRequest request = new HttpRequest(null, requestHeader, null);

        // when
        HttpCookie cookie = request.getCookie();
        String actual = cookie.getJSessionId();

        // then
        assertThat(actual).isEqualTo(jSessionId);
    }
}
