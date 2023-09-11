package nextstep.jwp.presentation;

import org.apache.coyote.http11.response.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController {

    private static final IndexController INSTANCE = new IndexController();

    private IndexController() {
    }

    public static IndexController getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = FileReader.read(httpRequest.getUri());
        return httpResponse
                .addBaseHeader(httpRequest.getContentType())
                .addBody(body);
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        return null;
    }
}
