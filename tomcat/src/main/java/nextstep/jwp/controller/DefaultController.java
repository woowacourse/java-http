package nextstep.jwp.controller;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestUri;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.utils.FileUtils;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        RequestUri requestUri = request.getRequestLine().getRequestUri();

        response = new HttpResponse.Builder()
                .contentType(ContentType.from(requestUri.getExtension()))
                .body(FileUtils.readFile(requestUri.getPath()))
                .build();
    }
}
