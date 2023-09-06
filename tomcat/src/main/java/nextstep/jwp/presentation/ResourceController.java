package nextstep.jwp.presentation;

import nextstep.jwp.util.FileIOReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController implements Controller {

    private static final ResourceController instance = new ResourceController();

    private ResourceController() {
    }

    public static ResourceController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse service(HttpRequest request, HttpResponse response) {
        String responseBody = FileIOReader.readFile(request.getRequestUrl());
        return response.contentType(request.getAccept())
                       .body(responseBody);
    }
}
