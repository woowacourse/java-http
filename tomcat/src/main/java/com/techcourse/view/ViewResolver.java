package com.techcourse.view;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class ViewResolver {

    private static final String DEFAULT_ROUTE = "/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final ResponseBuilder responseBuilder = new ResponseBuilder();

    public HttpResponse resolve(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();

        if (DEFAULT_ROUTE.equals(path)) {
            response.setStatusCode(HttpStatusCode.OK);
            response.setBody(DEFAULT_RESPONSE_BODY);
            response.setContentType("text/html");
            return response;
        }

        URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            path += ".html";
        }

        return handleFileExtensionRequest(response, path);
    }

    private HttpResponse handleFileExtensionRequest(HttpResponse response, String path) throws IOException {
        URL staticResourceUrl = getClass().getClassLoader().getResource("static" + path);
        if (staticResourceUrl == null) {
            response.setStatusCode(HttpStatusCode.NOT_FOUND);
            response.setBody(responseBuilder.buildNotFoundResponse());
            response.setContentType("text/html");
            return response;
        }

        File file = new File(staticResourceUrl.getFile());
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());
        response.setStatusCode(HttpStatusCode.OK);
        response.setBody(new String(Files.readAllBytes(file.toPath())));
        response.setContentType(mimeType);
        return response;
    }
}
