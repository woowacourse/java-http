package mvc.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.application.UserService;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.response.HttpStatusCode;
import org.assertj.core.api.SoftAssertions;
import servlet.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import servlet.response.HttpResponse;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LoginControllerTest {

    @Test
    void 생성자는_경로와_rootContextPath를_전달하면_LoginHandler를_초기화한다() {
        final LoginController actual = new LoginController("/login", new UserService());

        assertThat(actual).isNotNull();
    }

    @Test
    void supports_메서드는_지원하는_요청인_경우_true를_반환한다() {
        final LoginController controller = new LoginController("/login", new UserService());
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: application/json");
        final RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromBodyContent("account=gugu&password=password")
        );
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());

        final boolean actual = controller.supports(httpRequest);

        assertThat(actual).isTrue();
    }

    @Test
    void supports_메서드는_지원하지_않는_요청인_경우_false를_반환한다() {
        final LoginController controller = new LoginController("/login", new UserService());
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());

        final boolean actual = controller.supports(httpRequest);

        assertThat(actual).isFalse();
    }

    @Test
    void service_메서드는_POST_요청을_처리하고_Response를_반환한다() throws Exception {
        final LoginController controller = new LoginController("/login", new UserService());
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: application/json");
        final RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1");
        final Request request = new Request(
                headers,
                requestLine,
                HttpRequestBody.EMPTY,
                Parameters.fromBodyContent("account=gugu&password=password")
        );
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());
        final HttpResponse httpResponse = new HttpResponse();

        controller.service(httpRequest, httpResponse);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(httpResponse.statusCode()).isEqualTo(HttpStatusCode.FOUND);
            softAssertions.assertThat(httpResponse.headerDtos()).hasSize(1);
        });
    }

    @Test
    void service_메서드는_GET_요청을_처리하고_Response를_반환한다() throws Exception {
        final LoginController controller = new LoginController("/login", new UserService());
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /hello HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());
        final HttpResponse httpResponse = new HttpResponse();

        controller.service(httpRequest, httpResponse);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(httpResponse.statusCode()).isEqualTo(HttpStatusCode.FOUND);
            softAssertions.assertThat(httpResponse.headerDtos()).hasSize(1);
        });
    }
}
