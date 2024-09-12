package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("path값을 가져올 수 있다.")
    @Test
    void getPath() {
        // given
        RequestLine requestLine = new RequestLine("GET /url/path?name=value HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of());
        String responseBody = "";
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, responseBody);

        // when
        String path = request.getPath();

        // then
        assertThat(path).isEqualTo("/url/path");
    }

    @DisplayName("GET의 queryParam을 가져올 수 있다.")
    @Test
    void getQueryParamWithGetMethod() {
        // given
        RequestLine requestLine = new RequestLine("GET /url/path?name=value HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of());
        String responseBody = "";
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, responseBody);

        // when
        QueryParam queryParam = request.getQueryParam();
        String actual = queryParam.getValue("name");

        // then
        assertThat(actual).isEqualTo("value");
    }

    @DisplayName("POST의 queryParam을 가져올 수 있다.")
    @Test
    void getQueryParamWithPostMethod() {
        // given
        RequestLine requestLine = new RequestLine("POST /url/path HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of());
        String responseBody = "name=value";
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, responseBody);

        // when
        QueryParam queryParam = request.getQueryParam();
        String actual = queryParam.getValue("name");

        // then
        assertThat(actual).isEqualTo("value");
    }

    @DisplayName("Cookie 값을 가져올 수 있다.")
    @Test
    void getCookie() {
        // given
        RequestLine requestLine = new RequestLine("POST /url/path HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Cookie: name=value"));
        String responseBody = "";
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, responseBody);

        // when
        HttpCookies cookies = request.getCookies();
        String actual = cookies.getCookieValue("name");

        // then
        assertThat(actual).isEqualTo("value");
    }
}
