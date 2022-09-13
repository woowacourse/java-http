package org.apache.coyote.support;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.util.FileUtils;

public class NotFoundController extends AbstractController {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        String body = FileUtils.readAllBytes("/404.html");
        response.errorForward(HttpStatus.NOT_FOUND, request.getPath());
        response.setBody(body);
        response.write();
    }
}
