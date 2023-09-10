package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.response.StatusLine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public abstract class AbstractController implements Controller {

    private static final String STATIC_PATH = "static";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isSameMethod(Method.POST)) {
            doPost(request, response);
            return;
        }
        if (request.isSameMethod(Method.GET)) {
            doGet(request, response);
            return;
        }

        final HttpResponse httpResponse = getRedirectResponse("/404.html");
        response.copy(httpResponse);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final HttpResponse httpResponse = getRedirectResponse("/500.html");
        response.copy(httpResponse);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final HttpResponse httpResponse = getRedirectResponse("/500.html");
        response.copy(httpResponse);
    }

    protected static HttpResponse getRedirectResponse(final String location) {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.FOUND);

        return HttpResponse.ofRedirect(statusLine, location);
    }

    protected static String getFileToResponseBody(final String fileName) throws IOException {
        final String path = STATIC_PATH + fileName;
        final URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        final String filePath = Objects.requireNonNull(resource).getPath();
        final File file = new File(URLDecoder.decode(filePath, StandardCharsets.UTF_8));

        return new String(Files.readAllBytes(file.toPath()));
    }
}

