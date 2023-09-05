package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class MainPageController implements Controller {

    private static final MainPageController instance = new MainPageController();

    private MainPageController() {
    }

    public static MainPageController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse service(HttpRequest request, HttpResponse response) {
        String body = "Hello world!";
        return response.contentType(request.getAccept())
                       .statusCode(StatusCode.OK)
                       .protocol(request.getProtocolVersion())
                       .body(body);
    }
}
