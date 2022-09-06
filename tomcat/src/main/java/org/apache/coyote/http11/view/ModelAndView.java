package org.apache.coyote.http11.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.handler.ApiHandler.ApiHandlerResponse;
import org.apache.coyote.http11.handler.FileHandler.FileHandlerResponse;
import org.apache.coyote.http11.httpmessage.request.Headers;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class ModelAndView {

    private final HttpStatus httpStatus;
    private final Headers headers;
    private final String view;
    private final ContentType contentType;

    private ModelAndView(HttpStatus httpStatus, Headers headers, String view, ContentType contentType) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.view = view;
        this.contentType = contentType;
    }

    public static ModelAndView of(Object handlerResponse) throws IOException {
        if (handlerResponse instanceof ApiHandlerResponse) {
            ApiHandlerResponse apiResponse = (ApiHandlerResponse) handlerResponse;
            return getApiHandlerResponse(apiResponse);
        }
        FileHandlerResponse fileResponse = (FileHandlerResponse) handlerResponse;
        return getFileResponse(fileResponse);
    }

    private static ModelAndView getApiHandlerResponse(ApiHandlerResponse response) throws IOException {
        HttpStatus httpStatus = response.getHttpStatus();
        Headers responseHeaders = response.getHeaders();
        String responseBody = response.getBody();
        ContentType contentType = response.getContentType();

        if (httpStatus.isFound() || responseBody.isBlank()) {
            return new ModelAndView(httpStatus, responseHeaders, responseBody, contentType);
        }

        URL resource = getResource(responseBody);
        if (resource == null) {
            return new ModelAndView(httpStatus, responseHeaders, responseBody, contentType);
        }

        return getModelAndView(httpStatus, responseHeaders, resource);
    }

    private static ModelAndView getFileResponse(FileHandlerResponse response) throws IOException {

        final String path = response.getPath();
        if (path.isBlank()) {
            return ModelAndView.of(new FileHandlerResponse(HttpStatus.NOT_FOUND, "/404.html"));
        }

        final URL resource = getResource(path);
        if (resource == null) {
            return ModelAndView.of(new FileHandlerResponse(HttpStatus.NOT_FOUND, "/404.html"));
        }

        return getModelAndView(response.getHttpStatus(), new Headers(new LinkedHashMap<>()), resource);
    }

    private static URL getResource(String path) {
        return ModelAndView.class.getClassLoader().getResource("static" + path);
    }

    private static ModelAndView getModelAndView(HttpStatus httpStatus, Headers headers, URL resource)
            throws IOException {
        File file = new File(resource.getFile());
        Path path = file.toPath();
        String view = new String(Files.readAllBytes(path));
        ContentType contentType = ContentType.of(Files.probeContentType(path));

        return new ModelAndView(httpStatus, headers, view, contentType);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getView() {
        return view;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
