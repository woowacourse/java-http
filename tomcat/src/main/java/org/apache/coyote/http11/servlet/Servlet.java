package org.apache.coyote.http11.servlet;

import jakarta.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.exception.HttpMethodNotAllowedException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public abstract class Servlet {

    private static final String RESOURCE_BASE_PATH = "static/";

    abstract void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        throw new HttpMethodNotAllowedException();
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new HttpMethodNotAllowedException();
    }

    protected String findResourceWithPath(String absolutePath) throws IOException {
        URL resourceUrl = Servlet.class.getClassLoader().getResource(RESOURCE_BASE_PATH + absolutePath);
        return new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
    }
}
