package org.apache.catalina.servlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.exception.CatalinaException;
import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.startline.HttpStatus;
import org.apache.catalina.http.startline.RequestURI;
import org.apache.catalina.util.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final String UTF_8_ENCODING = ";charset=utf-8";

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public abstract void service(HttpRequest request, HttpResponse response);

    protected void redirectTo(HttpResponse response, String target) {
        response.setStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeader.LOCATION, target);
    }

    protected void responseResource(HttpResponse response, String path) {
        RequestURI requestURI = new RequestURI(path);
        responseResource(response, requestURI.getPath());
    }

    protected void responseResource(HttpResponse response, Path path) {
        try {
            String responseBody = ResourceReader.read(path);
            String contentType = Files.probeContentType(path);
            response.addHeader(HttpHeader.CONTENT_TYPE, contentType + UTF_8_ENCODING);
            response.setBody(responseBody);
        } catch (NullPointerException | IOException e) {
            log.error(e.getMessage());
            throw new CatalinaException("Invalid path: " + path.toString());
        }
    }
}
