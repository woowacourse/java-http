package com.techcourse.servlet.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.servlet.StaticResourceServlet;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpMessageBody;
import org.apache.coyote.http11.common.HttpProtocol;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.request.line.Uri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("정적 자원 핸들러 매핑 테스트")
class ResourceHandlerMappingTest {

    private final ResourceHandlerMapping resourceHandlerMapping = new ResourceHandlerMapping();

    @DisplayName("요청 객체가 정적 자원에 대한 요청이고 이용 가능한 자원일 때 정적 자원 핸들러가 매핑된다")
    @Test
    void hasHandlerFor() {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, new Uri("/index.html"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpMessageBody httpMessageBody = HttpMessageBody.createEmptyBody();
        HttpServletRequest httpServletRequest = new HttpServletRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(resourceHandlerMapping.hasHandlerFor(httpServletRequest)).isTrue();
    }

    @DisplayName("요청 객체가 정적 자원에 대한 요청이 아니라면 정적 자원 핸들러가 매핑되지 않는다")
    @Test
    void hasNoHandlerFor() {
        // given
        RequestLine requestLine = new RequestLine(Method.POST, new Uri("/register"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpMessageBody httpMessageBody = HttpMessageBody.createEmptyBody();
        HttpServletRequest httpServletRequest = new HttpServletRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(resourceHandlerMapping.hasHandlerFor(httpServletRequest)).isFalse();
    }

    @DisplayName("요청 객체가 정적 자원에 대한 요청이더라도 이용 가능한 자원에 대한 요청이 아니면 핸들러가 매핑되지 않는다")
    @Test
    void hasNoHandlerForNotAvailableResource() {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, new Uri("/no-such-file.html"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpMessageBody httpMessageBody = HttpMessageBody.createEmptyBody();
        HttpServletRequest httpServletRequest = new HttpServletRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(resourceHandlerMapping.hasHandlerFor(httpServletRequest)).isFalse();
    }

    @DisplayName("정적 자원 요청에 대한 핸들러를 반환할 수 있다")
    @Test
    void getHandler() {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, new Uri("/index.html"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpMessageBody httpMessageBody = HttpMessageBody.createEmptyBody();
        HttpServletRequest httpServletRequest = new HttpServletRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(resourceHandlerMapping.getHandler(httpServletRequest)).isInstanceOf(StaticResourceServlet.class);
    }
}
