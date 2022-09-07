package nextstep.jwp.handler;

import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ErrorController implements Controller {

    private static final ErrorController INSTANCE = new ErrorController();

    public static Controller getInstance() {
        return INSTANCE;
    }

    private ErrorController() {
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest, HttpStatusCode.NOT_FOUND, "/404.html");
    }
}
