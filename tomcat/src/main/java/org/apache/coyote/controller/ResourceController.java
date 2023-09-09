package org.apache.coyote.controller;

import org.apache.coyote.FileReader.FileReader;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class ResourceController {

    public void getResource(Request request, Response response) {
        String path = request.getPath();
        FileReader fileReader = FileReader.from(path);
        String body = fileReader.read();

        if (fileReader.isFound()) {
            response.setHttpStatus(HttpStatus.OK);
        }
        if (!fileReader.isFound()) {
            response.setHttpStatus(HttpStatus.NOT_FOUND);
        }
        response.addHeaders("Content-Type", request.getResourceTypes());
        response.addHeaders("Content-Length", String.valueOf(body.getBytes().length));
        response.setResponseBody(body);
    }
}
