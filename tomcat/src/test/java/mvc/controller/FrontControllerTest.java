package mvc.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import mvc.controller.mapping.RequestMapping;
import nextstep.jwp.application.UserService;
import org.apache.coyote.http.SessionManager;
import servlet.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import servlet.response.HttpResponse;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FrontControllerTest {

    @Test
    void 생성자는_RequestMapping을_전달하면_FrontController를_초기화한다() {
        final List<AbstractPathController> controllers = List.of(new LoginController("/login", new UserService()));
        final RequestMapping requestMapping = new RequestMapping(controllers);

        final FrontController actual = new FrontController(requestMapping);

        assertThat(actual).isNotNull();
    }

    @Test
    void service_메서드는_유효한_HttpRequest와_HttpResponse를_전달하면_정상적으로_요청을_처리한다() {
        final List<AbstractPathController> controllers = List.of(new LoginController("/login", new UserService()));
        final RequestMapping requestMapping = new RequestMapping(controllers);
        final FrontController frontController = new FrontController(requestMapping);
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());
        final HttpResponse httpResponse = new HttpResponse();

        frontController.service(httpRequest, httpResponse);

        assertThat(httpResponse.contentType()).isEqualTo(ContentType.TEXT_HTML);
    }

    @Test
    void service_메서드는_유효하지_않은_HttpRequest와_HttpResponse를_전달하면_예외_페이지로_리다이렉션을_설정한다() {
        final List<AbstractPathController> controllers = List.of(new LoginController("/login", new UserService()));
        final RequestMapping requestMapping = new RequestMapping(controllers);
        final FrontController frontController = new FrontController(requestMapping);
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /hello HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());
        final HttpResponse httpResponse = new HttpResponse();

        frontController.service(httpRequest, httpResponse);

        assertThat(httpResponse.statusCode()).isEqualTo(HttpStatusCode.FOUND);
    }
}
