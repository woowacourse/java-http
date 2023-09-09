package org.apache.controller;

import java.util.Set;
import org.apache.controller.FileReader.FileReader;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class ResourceController extends AbstractController {

    private static final String URL = "";
    private static final Set<HttpMethod> AVAILABLE_HTTP_METHODS = Set.of(HttpMethod.GET);

    public ResourceController() {
        super(URL, AVAILABLE_HTTP_METHODS);
    }

    @Override
    protected void doGet(Request request, Response response) {
        FileReader fileReader = FileReader.from(request.getPath());
        String body = fileReader.read();

        setHttpResponse(response, fileReader);
        response.addHeaders(CONTENT_TYPE, request.getResourceTypes());
        response.addHeaders(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.setResponseBody(body);
    }

    private void setHttpResponse(Response response, FileReader fileReader) {
        if (fileReader.isFound()) {
            response.setHttpStatus(HttpStatus.OK);
            return;
        }
        response.setHttpStatus(HttpStatus.NOT_FOUND);
    }
}
