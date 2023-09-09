package nextstep.servlet;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DispatcherServletTest {

    @DisplayName("요청을 처리할 수 없는 경우 예외를 반환한다.")
    @Test
    void findControllerThrowException() {
        // given
        final DispatcherServlet dispatcherServlet = new DispatcherServlet();
        final HttpRequest httpRequest = new HttpRequest(new HttpHeaders(), HttpMethod.POST,
            HttpRequestURI.from("/not-found"), "HTTP/1.1", null);
        final HttpResponse basicResponse = HttpResponse.createBasicResponse();

        // when
        // then
        assertThatThrownBy(() -> dispatcherServlet.service(httpRequest, basicResponse))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
