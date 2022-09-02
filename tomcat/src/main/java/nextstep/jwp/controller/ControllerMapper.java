package nextstep.jwp.controller;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.exception.UnsupportedHttpMethodException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ControllerMapper {

    private final Controller controller;

    public ControllerMapper() {
        this.controller = new Controller();
    }

    public HttpResponse process(final HttpRequest httpRequest) {
        final var controller = new Controller();
        if (httpRequest.getMethod().equals(HttpMethod.GET)) {
            return doGet(controller, httpRequest);
        }
        if (httpRequest.getMethod().equals(HttpMethod.POST)) {
            return doPost(controller, httpRequest);
        }
        throw new UnsupportedHttpMethodException();
    }

    private HttpResponse doGet(final Controller controller, final HttpRequest httpRequest) {
        final var path = httpRequest.getPath();
        if (path.equals("/")) {
            return controller.showIndex();
        }
        if (path.equals("/login")) {
            return controller.showLogin(httpRequest.getSession());
        }
        if (path.equals("/register")) {
            return controller.showRegister();
        }
        return controller.show(path);
    }

    private HttpResponse doPost(final Controller controller, final HttpRequest httpRequest) {
        final var path = httpRequest.getPath();
        if (path.equals("/login")) {
            return controller.login(
                    httpRequest.getSession(),
                    httpRequest.parseBodyQueryString()
            );
        }
        if (path.equals("/register")) {
            return controller.register(httpRequest.parseBodyQueryString());
        }
        throw new NotFoundException("존재하지 않는 요청입니다.");
    }
}
