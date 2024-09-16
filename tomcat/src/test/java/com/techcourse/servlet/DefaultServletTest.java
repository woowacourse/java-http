package com.techcourse.servlet;

import static org.apache.coyote.http.HttpFixture.EMPTY_BODY;
import static org.apache.coyote.http.HttpFixture.EMPTY_HEADER;
import static org.apache.coyote.http.HttpFixture.INDEX_REQUEST_LINE;
import static org.apache.coyote.http.HttpFixture.NO_RESOURCE_LINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
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
    void service() {
        // given
        HttpRequest request = new HttpRequest(INDEX_REQUEST_LINE, EMPTY_HEADER, EMPTY_BODY);
        HttpResponse response = HttpResponse.createEmptyResponse();

        // when
        defaultServlet.doGet(request, response);

        //then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("존재하지 않는 파일을 서비스 하는 경우 예외가 발생한다")
    @Test
    void canNotServiceNonExistResource() {
        // given
        HttpRequest request = new HttpRequest(NO_RESOURCE_LINE, EMPTY_HEADER, EMPTY_BODY);
        HttpResponse response = HttpResponse.createEmptyResponse();

        // when & then
        assertThatThrownBy(() -> defaultServlet.doGet(request, response))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("존재하지 않는 파일입니다");
    }
}
