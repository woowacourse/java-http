package nextstep.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.Response;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RegisterPageHandlerTest {

    @Test
    void 생성자는_필요한_정보를_전달하면_RegisterPageHandler를_초기화한다() {
        final RegisterPageHandler actual = new RegisterPageHandler("/login","/index.html");

        assertThat(actual).isNotNull();
    }

    @Test
    void supports_메서드는_지원하는_요청인_경우_true를_반환한다() {
        final RegisterPageHandler handler = new RegisterPageHandler("/login","/index.html");
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = handler.supports(request, "/");

        assertThat(actual).isTrue();
    }

    @Test
    void supports_메서드는_지원하지_않는_요청인_경우_false를_반환한다() {
        final RegisterPageHandler handler = new RegisterPageHandler("/login","/index.html");
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /hello HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);

        final boolean actual = handler.supports(request, "/");

        assertThat(actual).isFalse();
    }

    @Test
    void service_메서드는_요청을_처리하고_Response를_반환한다() throws IOException {
        final RegisterPageHandler handler = new RegisterPageHandler("/login","/index.html");
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /hello HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        request.initSessionManager(new SessionManager());

        final Response actual = handler.service(request, "static/");

        assertThat(actual.convertResponseMessage()).contains("<!DOCTYPE html>");
    }
}
