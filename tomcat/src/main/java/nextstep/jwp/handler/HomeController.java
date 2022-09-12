package nextstep.jwp.handler;

import java.util.Objects;
import org.apache.coyote.http11.handler.HandlerResponseEntity;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class HomeController extends HttpRequestHandler {

    private static final HomeController instance = new HomeController();
    private static final String HOME_BODY = "Hello world!";

    private HomeController() {}

    public static HomeController getInstance() {
        return instance;
    }

    @Override
    public HandlerResponseEntity doGet(final HttpRequest httpRequest, final HttpResponseHeader responseHeader) {
        return HandlerResponseEntity.createWithResource(HOME_BODY);
    }

    @Override
    public HandlerResponseEntity doPost(final HttpRequest httpRequest, final HttpResponseHeader responseHeader) {
        throw new IllegalStateException("Invalid Uri");
    }
}
