package com.techcourse.servlet.mapping;

import static org.apache.coyote.http11.common.HttpProtocol.HTTP_11;
import static org.apache.coyote.http11.request.line.Method.GET;
import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.servlet.LoginPageServlet;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpMessageBody;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.request.line.Uri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("요청 매핑 핸들러 조회 테스트")
class RequestMappingHandlerMappingTest {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();

    @DisplayName("매핑 정보가 일치하는 핸들러가 존재하는지 확인할 수 있다.")
    @Test
    void hasHandlerFor() {
        // given
        RequestLine requestLine = new RequestLine(GET, new Uri("/login"), HTTP_11);
        HttpServletRequest httpServletRequest = new HttpServletRequest(
                requestLine,
                new HttpHeaders(),
                HttpMessageBody.createEmptyBody()
        );
        // when & then
        assertThat(requestMappingHandlerMapping.hasHandlerFor(httpServletRequest)).isTrue();
    }

    @DisplayName("매핑 정보가 일치하는 핸들러가 없는 경우를 확인할 수 있다")
    @Test
    void hasNoHandlerFor() {
        // given
        RequestLine requestLine = new RequestLine(GET, new Uri("/weird"), HTTP_11);
        HttpServletRequest httpServletRequest = new HttpServletRequest(
                requestLine,
                new HttpHeaders(),
                HttpMessageBody.createEmptyBody()
        );
        // when & then
        assertThat(requestMappingHandlerMapping.hasHandlerFor(httpServletRequest)).isFalse();
    }

    @DisplayName("매핑 정보가 일치하는 핸들러를 가져올 수 있다")
    @Test
    void getHandler() {
        // given
        RequestLine requestLine = new RequestLine(GET, new Uri("/login"), HTTP_11);
        HttpServletRequest httpServletRequest = new HttpServletRequest(
                requestLine,
                new HttpHeaders(),
                HttpMessageBody.createEmptyBody()
        );
        // when & then
        assertThat(requestMappingHandlerMapping.getHandler(httpServletRequest)).isInstanceOf(LoginPageServlet.class);
    }
}
