package mvc.controller.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import mvc.controller.AbstractPathController;
import mvc.controller.LoginController;
import mvc.controller.mapping.exception.EmptyControllerException;
import mvc.controller.mapping.exception.UnsupportedHttpRequestException;
import nextstep.jwp.application.UserService;
import org.apache.coyote.http.SessionManager;
import servlet.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import servlet.Controller;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestMappingTest {

    @Test
    void 생성자는_관리하는_컨트롤러를_전달하면_RequestMapping을_초기화한다() {
        final List<AbstractPathController> controllers = List.of(new LoginController("/login", new UserService()));

        final RequestMapping actual = new RequestMapping(controllers);

        assertThat(actual).isNotNull();
    }

    @Test
    void 생성자는_빈_list를_전달하면_예외가_발생한다() {
        assertThatThrownBy(() -> new RequestMapping(Collections.emptyList()))
                .isInstanceOf(EmptyControllerException.class)
                .hasMessageContaining("등록된 Controller가 존재하지 않습니다.");
    }

    @Test
    void getController_메서드는_요청을_처리할수_있는_컨트롤러가_있다면_해당_컨트롤러를_반환한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());
        final List<AbstractPathController> controllers = List.of(new LoginController("/login", new UserService()));
        final RequestMapping requestMapping = new RequestMapping(controllers);

        final Controller actual = requestMapping.getController(httpRequest);

        assertThat(actual).isNotNull();
    }

    @Test
    void getController_메서드는_요청을_처리할수_있는_컨트롤러가_없다면_예외가_발생한다() {
        final HttpRequestHeaders headers = HttpRequestHeaders.from("Content-Type: text/html;charset=utf-8");
        final RequestLine requestLine = RequestLine.from("GET /hello HTTP/1.1");
        final Request request = new Request(headers, requestLine, HttpRequestBody.EMPTY, Parameters.EMPTY);
        final HttpRequest httpRequest = new HttpRequest(request, "/", new SessionManager());
        final List<AbstractPathController> controllers = List.of(new LoginController("/login", new UserService()));
        final RequestMapping requestMapping = new RequestMapping(controllers);

        assertThatThrownBy(() -> requestMapping.getController(httpRequest))
                .isInstanceOf(UnsupportedHttpRequestException.class)
                .hasMessageContaining("해당 요청을 처리할 수 없습니다.");
    }
}
