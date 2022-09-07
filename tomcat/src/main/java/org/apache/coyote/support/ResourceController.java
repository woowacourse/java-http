package org.apache.coyote.support;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.util.FileUtils;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String body = FileUtils.readAllBytes(request.getPath().getValue());
        response.setStatus(HttpStatus.OK);
        response.forward(request.getPath());
        response.setBody(body);
        response.flush();
    }
}
