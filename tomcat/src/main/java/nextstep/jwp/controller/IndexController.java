package nextstep.jwp.controller;

import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

public class IndexController extends AbstractController {

    private static final IndexController INSTANCE = new IndexController();

    public static IndexController getInstance() {
        return INSTANCE;
    }

    private IndexController() {
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String contentType = ContentType.HTML.getContentType();

        response.statusCode(HttpStatus.OK);
        response.addHeader(HttpHeaderType.CONTENT_TYPE, contentType);
        response.responseBody("Hello world!");
    }
}
