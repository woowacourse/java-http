package nextstep.handler;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.handler.WelcomeHandler;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.Url;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HttpMethod;
import org.apache.coyote.http.util.HttpVersion;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WelcomeHandlerTest {

    @Test
    void 생성자는_rootContextPath를_전달하면_WelcomeHandler를_초기화한다() {
        final WelcomeHandler actual = new WelcomeHandler();

        assertThat(actual).isNotNull();
    }

    @Test
    void supports_메서드는_지원하는_요청인_경우_true를_반환한다() {
        final WelcomeHandler handler = new WelcomeHandler();
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
                Parameters.EMPTY
        );

        final boolean actual = handler.supports(request,"/");

        assertThat(actual).isTrue();
    }

    @Test
    void supports_메서드는_지원하지_않는_요청인_경우_false를_반환한다() {
        final WelcomeHandler handler = new WelcomeHandler();
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
                Parameters.EMPTY
        );

        final boolean actual = handler.supports(request, "/hello");

        assertThat(actual).isFalse();
    }

    @Test
    void service_메서드는_요청을_처리하고_Response를_반환한다() {
        final WelcomeHandler handler = new WelcomeHandler();
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
                Parameters.EMPTY
        );

        final Response actual = handler.service(request, "ignored");

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isNotNull();
            softAssertions.assertThat(actual.convertResponseMessage())
                          .contains("HTTP/1.1")
                          .contains("200 OK")
                          .contains("Content-Type: text/html;charset=utf-8")
                          .contains("Content-Length: 12")
                          .contains("Hello World!");
        });
    }
}
