package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.utils.Constants;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    private static final String ROOT_PATH = "/";
    private static final String FILE_EXTENSION_DELIMITER = ".";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isPostMethod()) {
            doPost(request, response);
        }
        if (request.isGetMethod()) {
            doGet(request, response);
        }
        setHttpResponse(request, response);
    }

    private void setHttpResponse(HttpRequest request, HttpResponse response) throws IOException {
        String location = getLocation(request, response);
        String contentType = FileReader.probeContentType(location);
        response.setContentType(contentType);

        String responseBody = FileReader.read(location);
        response.setContentLength(responseBody.getBytes().length);
        response.setBody(responseBody);
    }

    private String getLocation(HttpRequest request, HttpResponse response) {
        if (response.hasLocation()) {
            return response.getLocation();
        }
        String path = request.getPath();
        if (ROOT_PATH.equals(path)) {
            return Constants.DEFAULT_URI;
        }
        if (!path.contains(FILE_EXTENSION_DELIMITER)) {
            return path + Constants.EXTENSION_OF_HTML;
        }
        return path;
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response);

    protected abstract void doGet(HttpRequest request, HttpResponse response);
}
