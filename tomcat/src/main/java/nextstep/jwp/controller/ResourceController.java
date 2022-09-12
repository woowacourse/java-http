package nextstep.jwp.controller;

import nextstep.jwp.util.Parser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController implements Handler {

    private static final ResourceController INSTANCE = new ResourceController();

    private ResourceController() {
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String path = httpRequest.getUrl();
        final String fileName = Parser.convertResourceFileName(path);
        httpResponse.setOkResponse(fileName);
    }

    public static ResourceController getINSTANCE() {
        return INSTANCE;
    }
}
