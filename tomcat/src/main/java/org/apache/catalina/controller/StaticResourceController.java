package org.apache.catalina.controller;

import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class StaticResourceController implements Controller {

    public static final StaticResourceController INSTANCE = new StaticResourceController();

    private StaticResourceController() {
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        byte[] responseBody = StaticResourceReader.readResource(getClass().getClassLoader(), request.getUri());
        makeResponse(request, response, responseBody);
    }

    private void makeResponse(HttpRequest request, HttpResponse response, byte[] responseBody) {
        String contentType = ContentType.from(request.getUri()).getMimeType();
        response.setHeader(Header.CONTENT_TYPE.value(), contentType);
        response.setBody(responseBody);
    }
}
