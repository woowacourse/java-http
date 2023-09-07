package servlet.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 생성자는_Request를_전달하면_HttpRequest를_초기화한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final HttpRequest actual = new HttpRequest(request, "/", new SessionManager());

        assertThat(actual).isNotNull();
    }

    @Test
    void getHeader_메서드는_전달한_헤더_이름의_값을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());

        final String actual = httpRequest.getHeader("Content-Type");

        assertThat(actual).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void 파라미터가_없는_getSession_메서드는_세션이_있으면_세션을_반환한다() {
        final SessionManager sessionManager = new SessionManager();
        final HttpSession expected = new HttpSession();
        sessionManager.add(expected);
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8 \r\n Cookie: JSESSIONID=" + expected.getId());
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.EMPTY,
                HttpCookie.fromSessionId(expected.getId())
        );
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());

        final HttpSession actual = httpRequest.getSession();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 파라미터가_없는_getSession_메서드는_세션이_없으면_세션을_생성해_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());

        final HttpSession actual = httpRequest.getSession();

        assertThat(actual).isNotNull();
    }

    @Test
    void getSession_메서드에_false를_전달하면_세션이_있으면_세션을_반환한다() {
        final SessionManager sessionManager = new SessionManager();
        final HttpSession expected = new HttpSession();
        sessionManager.add(expected);
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8 \r\n Cookie: JSESSIONID=" + expected.getId());
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.EMPTY,
                HttpCookie.fromSessionId(expected.getId())
        );
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());

        final HttpSession actual = httpRequest.getSession(false);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getSession_메서드에_false를_전달하면_세션이_없으면_null을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());

        final HttpSession actual = httpRequest.getSession(false);

        assertThat(actual).isNull();
    }

    @Test
    void getParameter_메서드는_전달한_파라미터_이름의_값을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login?user=gugu HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromUrlContent("?user=gugu")
        );
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());

        final String actual = httpRequest.getParameter("user");

        assertThat(actual).isEqualTo("gugu");
    }

    @Test
    void isBusinessLogic_메서드는_전달한_요청이_정적_리소스가_아닌_비즈니스_로직인지_여부를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login?user=gugu HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromUrlContent("?user=gugu")
        );
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());

        final boolean actual = httpRequest.isBusinessLogic("/");

        assertThat(actual).isTrue();
    }
}
