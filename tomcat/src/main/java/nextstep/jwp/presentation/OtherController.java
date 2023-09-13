package nextstep.jwp.presentation;

import org.apache.coyote.http11.response.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class OtherController extends AbstractController {

    private static final OtherController INSTANCE = new OtherController();

    private OtherController() {
    }

    public static OtherController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = FileReader.read(httpRequest.getUri());
        return httpResponse
                .addBaseHeader(httpRequest.getContentType())
                .addBody(body);
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        return null;
    }
}
