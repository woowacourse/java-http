package org.apache.coyote.controller;

import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.http.response.ResponseHeader;
import org.apache.coyote.http.response.StatusLine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.coyote.util.Constants.STATIC_RESOURCE_LOCATION;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method = request.getMethod();
        if (method.equals(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (method.equals(HttpMethod.POST)) {
            doPost(request, response);
        }
        throw new UnsupportedOperationException(method.getMethod());
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException(request.getMethod().getMethod());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException(request.getMethod().getMethod());
    }

    protected void generateStaticResponse(String path, HttpStatus status, HttpResponse response) throws IOException, NullPointerException {
        final URL resource = HttpResponse.class.getClassLoader().getResource(STATIC_RESOURCE_LOCATION + path);
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        StatusLine statusLine = new StatusLine(status);

        ResponseHeader header = new ResponseHeader();
        header.setContentType(MimeType.getContentTypeFromExtension(path));
        header.setContentLength(responseBody.getBytes().length);

        response.setResponse(statusLine, header, responseBody);
    }
}
