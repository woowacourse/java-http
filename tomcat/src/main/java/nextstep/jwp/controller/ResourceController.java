package nextstep.jwp.controller;

import nextstep.jwp.util.Parser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController extends AbstractController {

    private static final ResourceController INSTANCE = new ResourceController();

    private ResourceController() {
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String path = request.getUrl();
        final String fileName = Parser.convertResourceFileName(path);
        response.setOkResponse(fileName);
    }

    public static ResourceController getINSTANCE() {
        return INSTANCE;
    }
}
