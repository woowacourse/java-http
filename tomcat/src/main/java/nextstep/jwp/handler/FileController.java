package nextstep.jwp.handler;

import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FileController extends AbstractController {

    private static final Controller INSTANCE = new FileController();

    public static Controller getInstance() {
        return INSTANCE;
    }

    private FileController() {
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return generateResponse(httpRequest);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return generateResponse(httpRequest);
    }

    private HttpResponse generateResponse(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest, HttpStatusCode.OK, httpRequest.getUrl());
    }
}
