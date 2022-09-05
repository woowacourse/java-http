package nextstep.jwp.presentation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class ResourceController extends AbstractController {

    private static final String PREFIX = "static";

    public static ResourceController instance = new ResourceController();

    private ResourceController() {
    }

    public static ResourceController getInstance() {
        return instance;
    }

    @Override
    void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(PREFIX + httpRequest.getRequestUri());
        try {
            File file = new File(url.getFile());
            httpResponse.addResponseBody(file);
        } catch (NullPointerException e) {
            httpResponse.addHttpStatus(HttpStatus.NOT_FOUND);
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/404.html").getFile());
            httpResponse.addResponseBody(file);
        }
    }

    @Override
    void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {

    }
}
