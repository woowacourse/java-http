package org.apache.coyote.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.QueryParameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.Url;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HttpMethod;
import org.apache.coyote.http.util.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LoginHandlerTest {

    @Test
    void 생성자는_경로와_rootContextPath를_전달하면_LoginHandler를_초기화한다() {
        final LoginHandler actual = new LoginHandler("/login", "/");

        assertThat(actual).isNotNull();
    }

    @Test
    void supports_메서드는_지원하는_요청인_경우_true를_반환한다() {
        final LoginHandler handler = new LoginHandler("/login", "/");
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: application/json");
        final HttpMethod method = HttpMethod.findMethod("post");
        final Url url = Url.from("/login");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.fromBodyContent("account=gugu&password=password")
        );

        final boolean actual = handler.supports(request);

        assertThat(actual).isTrue();
    }

    @Test
    void supports_메서드는_지원하지_않는_요청인_경우_false를_반환한다() {
        final LoginHandler handler = new LoginHandler("/login", "/");
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

        final boolean actual = handler.supports(request);

        assertThat(actual).isFalse();
    }

    @Test
    void service_메서드는_요청을_처리하고_Response를_반환한다() throws IOException {
        final LoginHandler handler = new LoginHandler("/login", "/");
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: application/json");
        final HttpMethod method = HttpMethod.findMethod("post");
        final Url url = Url.from("/login");
        final HttpVersion version = HttpVersion.findVersion("HTTP/1.1");
        final Request request = new Request(
                headers,
                method,
                version,
                url,
                HttpRequestBody.EMPTY,
                QueryParameters.fromUrlContent("?account=gugu&password=password")
        );
        request.initSessionManager(new SessionManager());

        final Response actual = handler.service(request);

        assertThat(actual.convertResponseMessage()).contains("302 Found");
    }
}
