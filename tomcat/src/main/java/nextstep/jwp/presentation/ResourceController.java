package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class ResourceController extends AbstractController {

    public static ResourceController instance = new ResourceController();

    private ResourceController() {
    }

    public static ResourceController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        try {
            httpResponse.sendResource(httpRequest.getRequestUri());
        } catch (NullPointerException e) {
            httpResponse.sendError(HttpStatus.NOT_FOUND, "/404.html");
        }
    }
}
