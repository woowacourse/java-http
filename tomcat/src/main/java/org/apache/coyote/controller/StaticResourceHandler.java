package org.apache.coyote.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.session.Session;

public class StaticResourceHandler implements Handler {

    private static final String STATIC_RESOURCE_PATH = "static";
    private static final StaticResourceHandler INSTANCE = new StaticResourceHandler();

    private StaticResourceHandler() {
    }

    public static StaticResourceHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        Session session = request.getSession();
        if (request.getUri().contains("/login") && session.getAttribute("user") != null) {
            return new HttpResponse(
                    StatusCode.FOUND,
                    Map.of(Header.LOCATION.value(), "/index.html",
                           Header.SET_COOKIE.value(), "JSESSIONID=" + request.getSession().getId()),
                    null);
        }
        return handle(request, StatusCode.OK);
    }

    public HttpResponse handle(HttpRequest request, StatusCode statusCode) {
        File responseBody = getStaticResource(request.getUri());
        try {
            return makeResponse(responseBody, statusCode);
        } catch (IOException e) {
            return new HttpResponse(StatusCode.INTERNAL_SERVER_ERROR, Map.of(), null);
        }
    }

    private File getStaticResource(String location) {
        if (location.equals("/")) {
            location = "/hello.html";
        }
        if (!location.contains(".")) {
            location += ".html";
        }
        File file;
        try {
            file = new File(getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + location).getFile());
        } catch (NullPointerException e) {
            file = new File(getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + "/404.html").getFile());
        }
        return file;
    }

    private HttpResponse makeResponse(File resource, StatusCode statusCode) throws IOException {
        byte[] responseBody = Files.readAllBytes(resource.toPath());
        return new HttpResponse(statusCode,
                                Map.of("Content-Type", getContentType(resource),
                                   "Content-Length", getResponseLength(responseBody)),
                                new String(responseBody));
    }

    private String getContentType(File resource) {
        return ContentType.of(resource).getMimeType();
    }

    private String getResponseLength(byte[] responseBody) {
        return String.valueOf(responseBody.length);
    }
}
