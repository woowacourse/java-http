package nextstep.jwp.servlet;

import nextstep.jwp.http.MockOutputStream;
import org.apache.coyote.*;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.RequestInfo;
import org.apache.coyote.support.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DispatcherServletTest {

    private final DispatcherServlet dispatcherServlet = new DispatcherServlet();

    @Test
    void 로그인_페이지_접속시_세션이_존재하면_index로_리다이렉션된다() {
        // given
        final String sessionId = "sessionId";
        세션이_존재한다(sessionId);
        final Request request = 쿠키에_세션정보가_존재하는_요청을_생성한다(sessionId);
        final Response response = new Response(new MockOutputStream());
        final Response expected = new Response(new MockOutputStream())
                .httpStatus(HttpStatus.FOUND)
                .header(HttpHeader.LOCATION, "/index.html");

        // when
        dispatcherServlet.service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("outputStream")
                .isEqualTo(expected);
    }

    private void 세션이_존재한다(final String sessionId) {
        final SessionManager sessionManager = SessionManager.get();
        sessionManager.add(new Session(sessionId));
    }

    private Request 쿠키에_세션정보가_존재하는_요청을_생성한다(final String sessionId) {
        final Headers headers = new Headers();
        headers.put(HttpHeader.COOKIE, "JSESSIONID=" + sessionId);
        return new Request(new RequestInfo(HttpMethod.GET, "/login"), headers, null);
    }
}
