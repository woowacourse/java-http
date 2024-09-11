package org.apache.coyote.controller;

import java.io.IOException;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class StaticResourceController implements Controller {

    public static final StaticResourceController INSTANCE = new StaticResourceController();

    private StaticResourceController() {
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            byte[] responseBody = StaticResourceFinder.readResource(getClass().getClassLoader(), request.getUri());
            makeResponse(request, response, responseBody);
        } catch (IOException e) {
            response.setStatus(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void makeResponse(HttpRequest request, HttpResponse response, byte[] responseBody) throws IOException {
        String contentType = ContentType.from(request.getUri()).getMimeType();
        response.setHeader(Header.CONTENT_TYPE.value(), contentType);
        response.setBody(responseBody);
    }
}
