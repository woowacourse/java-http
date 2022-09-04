package org.apache.coyote.http11.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.handler.ApiHandler.ApiHandlerResponse;
import org.apache.coyote.http11.handler.FileHandler.FileHandlerResponse;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class ModelAndView {

    private final HttpStatus httpStatus;
    private final String view;
    private final ContentType contentType;

    private ModelAndView(HttpStatus httpStatus, String view, ContentType contentType) {
        this.httpStatus = httpStatus;
        this.view = view;
        this.contentType = contentType;
    }

    public static ModelAndView of(Object handlerResponse) throws IOException {
        if (handlerResponse instanceof ApiHandlerResponse) {
            return getApiHandlerResponse(handlerResponse);
        }
        return getFileHandlerResponse(handlerResponse);
    }

    private static ModelAndView getApiHandlerResponse(Object handlerResponse) throws IOException {
        ApiHandlerResponse response = (ApiHandlerResponse) handlerResponse;

        String body = (String) response.getBody();
        final URL resource = getResource(body);
        if (resource == null) {
            return new ModelAndView(response.getHttpStatus(), body, ContentType.HTML);
        }

        return getModelAndView(response.getHttpStatus(), body, resource);
    }

    private static ModelAndView getFileHandlerResponse(Object handlerResponse) throws IOException {
        final FileHandlerResponse response = (FileHandlerResponse) handlerResponse;

        final String path = response.getPath();
        final URL resource = getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException(path + "는 존재하지 않는 파일입니다.");
        }
        return getModelAndView(HttpStatus.OK, path, resource);
    }

    private static ModelAndView getModelAndView(HttpStatus httpStatus, String path, URL resource) throws IOException {
        File file = new File(resource.getFile());
        String view = new String(Files.readAllBytes(file.toPath()));
        ContentType contentType = ContentType.of(getFileType(path));

        return new ModelAndView(httpStatus, view, contentType);
    }

    private static URL getResource(String body) {
        return ModelAndView.class.getClassLoader().getResource("static" + body);
    }

    private static String getFileType(String body) {
        return body.split("\\.")[1];
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }


    public String getView() {
        return view;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
