package com.techcourse.servlet.mapping;

import static org.apache.coyote.http.HttpProtocol.HTTP_11;
import static org.apache.coyote.http.request.line.Method.GET;
import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.servlet.DefaultServlet;
import com.techcourse.servlet.LoginServlet;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.HttpProtocol;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.line.Method;
import org.apache.coyote.http.request.line.RequestLine;
import org.apache.coyote.http.request.line.Uri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("요청 매핑 핸들러 조회 테스트")
class ApplicationRequestMappingTest {

    private final ApplicationRequestMapping applicationRequestMapping = new ApplicationRequestMapping();

    @DisplayName("매핑 정보가 일치하는 핸들러를 가져올 수 있다")
    @Test
    void getHandler() {
        // given
        RequestLine requestLine = new RequestLine(GET, new Uri("/login"), HTTP_11);
        HttpRequest httpRequest = new HttpRequest(
                requestLine,
                new HttpHeaders(),
                HttpMessageBody.createEmptyBody()
        );
        // when & then
        assertThat(applicationRequestMapping.getServlet(httpRequest)).isInstanceOf(LoginServlet.class);
    }

    @DisplayName("매핑 정보가 일치하는 핸들러가 없다면 디폴트 서블릿이 반환된다")
    @Test
    void getDefaultHandler() {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, new Uri("/index.html"), HttpProtocol.HTTP_11);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpMessageBody httpMessageBody = HttpMessageBody.createEmptyBody();
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders, httpMessageBody);

        // when & then
        assertThat(applicationRequestMapping.getServlet(httpRequest)).isInstanceOf(DefaultServlet.class);
    }
}
