package org.apache.coyote.http.request.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.QueryParameters;
import org.apache.coyote.http.request.Url;
import org.apache.coyote.http.util.HttpMethod;
import org.apache.coyote.http.util.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestTest {

    @Test
    void 생성자는_필요한_값을_전달하면_Request를_초기화한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");

        final Request actual = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );

        assertThat(actual).isNotNull();
    }

    @Test
    void findHeaderValue_메서드는_전달한_헤더_name의_값을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );

        assertThat(request.findHeaderValue("Content-Type")).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void findQueryParameterValue_메서드는_전달한_쿼리_파라미터_name의_값을_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/login?user=gugu");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final QueryParameters queryParameters = QueryParameters.fromUrlContent("/login?user=gugu");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                queryParameters
        );

        final String actual = request.findQueryParameterValue("user");

        assertThat(actual).isEqualTo("gugu");
    }

    @Test
    void matchesByMethod_메서드는_전달한_http_메서드와_동일하면_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );

        final boolean actual = request.matchesByMethod(HttpMethod.GET);

        assertThat(actual).isTrue();
    }

    @Test
    void matchesByPath_메서드는_전달한_path와_일치하면_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/login?user=gugu");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final QueryParameters queryParameters = QueryParameters.fromUrlContent("/login?user=gugu");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                queryParameters
        );

        final boolean actual = request.matchesByPath("/login", "/");

        assertThat(actual).isTrue();
    }

    @Test
    void matchesByPath_메서드는_전달한_path와_일치하지_않으면_false를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/login?user=gugu");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final QueryParameters queryParameters = QueryParameters.fromUrlContent("/login?user=gugu");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                queryParameters
        );

        final boolean actual = request.matchesByPath("/login", "/hello");

        assertThat(actual).isFalse();
    }

    @Test
    void isWelcomePageRequest_메서드는_welcome_page_요청인_경우_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );

        final boolean actual = request.isWelcomePageRequest("/");

        assertThat(actual).isTrue();
    }

    @Test
    void isWelcomePageRequest_메서드는_welcome_page_요청이_아닌_경우_false를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );

        final boolean actual = request.isWelcomePageRequest("/hello");

        assertThat(actual).isFalse();
    }

    @Test
    void isStaticResource_메서드는_정적_리소스_요청인_경우_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );

        final boolean actual = request.isStaticResource();

        assertThat(actual).isTrue();
    }

    @Test
    void isStaticResource_메서드는_정적_리소스_요청이_아닌_경우_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/login?user=gugu");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final QueryParameters queryParameters = QueryParameters.fromUrlContent("/login?user=gugu");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                queryParameters
        );

        final boolean actual = request.isStaticResource();

        assertThat(actual).isFalse();
    }

    @Test
    void hasQueryParameters_메서드는_query_parameters가_있으면_true를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/login?user=gugu");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final QueryParameters queryParameters = QueryParameters.fromUrlContent("/login?user=gugu");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                queryParameters
        );

        final boolean actual = request.hasQueryParameters();

        assertThat(actual).isTrue();
    }

    @Test
    void hasQueryParameters_메서드는_query_parameters가_없으면_false를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final HttpMethod method = HttpMethod.findMethod("get");
        final Url url = Url.from("/index.html");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.EMPTY
        );

        final boolean actual = request.hasQueryParameters();

        assertThat(actual).isFalse();
    }
}
