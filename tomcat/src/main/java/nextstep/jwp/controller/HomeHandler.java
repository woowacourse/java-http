package nextstep.jwp.controller;

import org.apache.coyote.model.request.ContentType;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;

import static org.apache.coyote.model.request.RequestLine.getExtension;

public class HomeHandler extends AbstractHandler {

    private static final HomeHandler INSTANCE = new HomeHandler();
    private static final String HELLO_WORLD = "Hello world!";

    public HomeHandler() {
    }

    public static HomeHandler getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public String getResponse(final HttpRequest httpRequest) {
        return HttpResponse.of(ContentType.getType(getExtension(httpRequest.getPath())), HELLO_WORLD,
                        ResponseLine.of(StatusCode.OK))
                .getResponse();
    }
}
