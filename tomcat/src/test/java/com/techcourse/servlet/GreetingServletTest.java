package com.techcourse.servlet;

import static org.apache.coyote.http.HttpFixture.EMPTY_BODY;
import static org.apache.coyote.http.HttpFixture.EMPTY_HEADER;
import static org.apache.coyote.http.HttpFixture.HOME_REQUEST_LINE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.line.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("대문 페이지 요청 서블릿 테스트")
class GreetingServletTest {

    private final GreetingServlet greetingServlet = new GreetingServlet();

    @DisplayName("홈 요청을 처리할 수 있다")
    @Test
    void doServiceHome() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(HOME_REQUEST_LINE, EMPTY_HEADER, EMPTY_BODY);
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();

        // when
        greetingServlet.doService(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }
}
