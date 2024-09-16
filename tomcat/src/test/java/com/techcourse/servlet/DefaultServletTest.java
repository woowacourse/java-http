package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http.RequestResponseFixture;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.line.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("디폴트 서블릿 테스트")
class DefaultServletTest {

    private final DefaultServlet defaultServlet = new DefaultServlet();

    @DisplayName("static 하위에 있는 파일을 서비스 할 수 있다")
    @Test
    void service() throws IOException {
        // given
        HttpRequest request = RequestResponseFixture.INDEX_REQUEST;
        HttpResponse response = HttpResponse.createEmptyResponse();
        // when
        defaultServlet.doGet(request, response);
        //then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }
}
