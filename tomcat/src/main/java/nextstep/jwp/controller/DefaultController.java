package nextstep.jwp.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestUri;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.utils.FileUtils;

public class DefaultController extends AbstractController {

    private static final Controller INSTANCE = new DefaultController();

    public static Controller getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        RequestUri requestUri = request.getRequestLine().getRequestUri();

        response.setContentType(ContentType.from(requestUri.getExtension()));
        response.setBody(FileUtils.readFile(requestUri.getPath()));
    }
}
