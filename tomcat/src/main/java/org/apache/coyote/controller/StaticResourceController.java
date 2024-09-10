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

public class StaticResourceController implements Controller {

    private static final String STATIC_RESOURCE_PATH = "static";
    private static final StaticResourceController INSTANCE = new StaticResourceController();

    private StaticResourceController() {
    }

    public static StaticResourceController getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Session session = request.getSession();
        if (request.getUri().contains("/login") && session.getAttribute("user") != null) {
            response.setStatus(StatusCode.FOUND);
            response.setHeaders(Map.of(Header.LOCATION.value(), "/index.html",
                                       Header.SET_COOKIE.value(), "JSESSIONID=" + request.getSession().getId()));
            return;
        }
        handle(request, response, StatusCode.OK);
    }

    public void handle(HttpRequest request, HttpResponse response, StatusCode statusCode) {
        File responseBody = getStaticResource(request.getUri());
        try {
            makeResponse(response, responseBody, statusCode);
        } catch (IOException e) {
            response.setStatus(StatusCode.INTERNAL_SERVER_ERROR);
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

    private void makeResponse(HttpResponse response, File resource, StatusCode statusCode) throws IOException {
        byte[] responseBody = Files.readAllBytes(resource.toPath());
        Map<String, String> headers = Map.of(Header.CONTENT_TYPE.value(), getContentType(resource),
                                             Header.CONTENT_LENGTH.value(), getResponseLength(responseBody));
        response.setBody(responseBody);
        response.setStatus(statusCode);
        response.setHeaders(headers);
    }

    private String getContentType(File resource) {
        return ContentType.of(resource).getMimeType();
    }

    private String getResponseLength(byte[] responseBody) {
        return String.valueOf(responseBody.length);
    }
}
