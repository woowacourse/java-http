package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.util.HttpMethod;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestTest {

    @Test
    void 생성자는_필요한_값을_전달하면_Request를_초기화한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");

        final Request actual = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        assertThat(actual).isNotNull();
    }

    @Test
    void findHeaderValue_메서드는_전달한_헤더_name의_값을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login?user=gugu HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromUrlContent("?user=gugu")
        );

        assertThat(request.findHeaderValue("Content-Type")).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void findQueryParameterValue_메서드는_전달한_쿼리_파라미터_name의_값을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login?user=gugu HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromUrlContent("?user=gugu")
        );

        final String actual = request.findParameterValue("user");

        assertThat(actual).isEqualTo("gugu");
    }

    @Test
    void matchesByMethod_메서드는_전달한_http_메서드와_동일하면_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = request.matchesByMethod(HttpMethod.GET);

        assertThat(actual).isTrue();
    }

    @Test
    void matchesByPath_메서드는_전달한_path와_일치하면_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login?user=gugu HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromUrlContent("?user=gugu")
        );

        final boolean actual = request.matchesByPathExcludingContextPath("/login", "/");

        assertThat(actual).isTrue();
    }

    @Test
    void matchesByPath_메서드는_전달한_path와_일치하지_않으면_false를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login?user=gugu HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromUrlContent("?user=gugu")
        );

        final boolean actual = request.matchesByPathExcludingContextPath("/login", "/hello");

        assertThat(actual).isFalse();
    }

    @Test
    void matchesByRootContextPath_메서드는_path가_rootContextPath로_시작하면_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final String contextPath = "/";

        final boolean actual = request.matchesByContextPath(contextPath);

        assertThat(actual).isTrue();
    }

    @Test
    void matchesByRootContextPath_메서드는_path가_rootContextPath로_시작하지_않으면_false를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final String contextPath = "/hello";

        final boolean actual = request.matchesByContextPath(contextPath);

        assertThat(actual).isFalse();
    }

    @Test
    void isWelcomePageRequest_메서드는_welcome_page_요청인_경우_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET / HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final boolean actual = request.isWelcomePageRequest("/");

        assertThat(actual).isTrue();
    }

    @Test
    void isWelcomePageRequest_메서드는_welcome_page_요청이_아닌_경우_false를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = request.isWelcomePageRequest("/hello");

        assertThat(actual).isFalse();
    }

    @Test
    void isStaticResource_메서드는_정적_리소스_요청인_경우_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = request.isStaticResource();

        assertThat(actual).isTrue();
    }

    @Test
    void isStaticResource_메서드는_정적_리소스_요청이_아닌_경우_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login?user=gugu HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromUrlContent("?user=gugu")
        );

        final boolean actual = request.isStaticResource();

        assertThat(actual).isFalse();
    }

    @Test
    void hasQueryParameters_메서드는_query_parameters가_있으면_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login?user=gugu HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromUrlContent("?user=gugu")
        );

        final boolean actual = request.hasQueryParameters();

        assertThat(actual).isTrue();
    }

    @Test
    void hasQueryParameters_메서드는_query_parameters가_없으면_false를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = request.hasQueryParameters();

        assertThat(actual).isFalse();
    }

    @Test
    void getSession_메서드는_true를_전달하면_세션이_없을_시_새로운_세션을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final HttpSession actual = request.getSession(true, new SessionManager());

        assertThat(actual).isNotNull();
    }

    @Test
    void getSession_메서드는_false를_전달하면_세션이_없을_시_null을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final HttpSession actual = request.getSession(false, new SessionManager());

        assertThat(actual).isNull();
    }
}
