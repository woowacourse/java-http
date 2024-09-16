package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpHeaderName;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.HttpProtocol;
import org.apache.coyote.http.request.line.Method;
import org.apache.coyote.http.request.line.RequestLine;
import org.apache.coyote.http.request.line.Uri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("isGet은 RequestLine의 메서드가 GET일 때 true를 반환한다.")
    void isGet() {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, new Uri("/"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpMessageBody httpMessageBody = HttpMessageBody.createEmptyBody();

        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(httpRequest.isGet()).isTrue();
    }

    @Test
    @DisplayName("isPost는 RequestLine의 메서드가 POST일 때 true를 반환한다.")
    void isPost() {
        // given
        RequestLine requestLine = new RequestLine(Method.POST, new Uri("/"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpMessageBody httpMessageBody = HttpMessageBody.createEmptyBody();

        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(httpRequest.isPost()).isTrue();
    }

    @Test
    @DisplayName("getFormData는 HttpMessageBody에서 폼 데이터를 가져온다.")
    void getFormData() {
        // given
        RequestLine requestLine = new RequestLine(Method.POST, new Uri("/submit"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpMessageBody httpMessageBody = new HttpMessageBody("username=user123");

        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(httpRequest.getFormData("username")).isEqualTo("user123");
    }

    @Test
    @DisplayName("getSessionId는 HttpHeaders에서 쿠키를 가져와 JSESSIONID 값을 반환한다.")
    void getSessionId() {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, new Uri("/"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putHeader(HttpHeaderName.COOKIE, "JSESSIONID=sessionId123; OtherCookie=otherValue");

        HttpMessageBody httpMessageBody = HttpMessageBody.createEmptyBody();
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(httpRequest.getSessionId()).isEqualTo("sessionId123");
    }

    @Test
    @DisplayName("getMethod는 RequestLine의 메서드를 반환한다.")
    void getMethod() {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, new Uri("/update"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpMessageBody httpMessageBody = HttpMessageBody.createEmptyBody();

        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(httpRequest.getMethod()).isEqualTo(Method.GET);
    }
}
