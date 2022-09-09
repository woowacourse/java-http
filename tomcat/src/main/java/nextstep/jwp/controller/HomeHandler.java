package nextstep.jwp.controller;

import org.apache.coyote.model.request.ContentType;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;

import static org.apache.coyote.utils.Util.getExtension;

public class HomeHandler extends AbstractHandler {

    public static final String HELLO_WORLD = "Hello world!";
    private static final HomeHandler INSTANCE = new HomeHandler();

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
