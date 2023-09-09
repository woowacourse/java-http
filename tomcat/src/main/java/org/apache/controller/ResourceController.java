package org.apache.controller;

import org.apache.coyote.FileReader.FileReader;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class ResourceController extends AbstractController {

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
