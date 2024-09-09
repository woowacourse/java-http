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

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        HttpMethod method = request.getMethod();
        if (method.equals(HttpMethod.GET)) {
            return doGet(request);
        }
        if (method.equals(HttpMethod.POST)) {
            return doPost(request);
        }
        throw new UnsupportedOperationException(method.getMethod());
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException(request.getMethod().getMethod());
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException(request.getMethod().getMethod());
    }

    protected HttpResponse generateResponse(String path, HttpStatus status) throws IOException, NullPointerException {
        final URL resource = HttpResponse.class.getClassLoader().getResource(path);
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        StatusLine statusLine = new StatusLine(status);

        ResponseHeader header = new ResponseHeader();
        header.setContentType(MimeType.getContentTypeFromExtension(path));
        header.setContentLength(responseBody.getBytes().length);

        return new HttpResponse(statusLine, header, responseBody);
    }
}
