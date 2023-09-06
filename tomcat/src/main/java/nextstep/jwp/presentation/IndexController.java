package nextstep.jwp.presentation;

import nextstep.jwp.util.FileIOReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController implements Controller {

    private static final IndexController instance = new IndexController();

    private IndexController() {
    }

    public static IndexController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse service(HttpRequest request, HttpResponse response) {
        String responseBody = FileIOReader.readFile(request.getRequestUrl());
        return response.contentType(request.getAccept())
                       .body(responseBody);
    }
}
