package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.file.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.util.FileExtension;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();

        if (method.isGet()) {
            doGet(request, response);
        } else if (method.isPost()) {
            doPost(request, response);
        } else {
            throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
        }

        if (response.isRedirection()) {
            handleRedirection(response);
            return;
        }
        handleResponse(response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    private void handleRedirection(HttpResponse response) {
        String location = response.getLocation();
        response.setLocation(location);
        response.setMimeType(MimeType.from(FileExtension.from(location)));
        response.setBody("".getBytes());
    }

    private void handleResponse(HttpResponse response) {
        try {
            String location = response.getLocation();
            byte[] body = ResourceReader.read(location);
            response.setStatus(HttpStatusCode.OK);
            response.setMimeType(MimeType.from(FileExtension.from(location)));
            response.setBody(Arrays.toString(body).getBytes());
        } catch (URISyntaxException | IOException e) {
            response.setMimeType(MimeType.OTHER);
            response.setStatus(HttpStatusCode.NOT_FOUND);
            response.setBody("No File Found".getBytes());
        }
    }
}
