package org.apache.coyote.http11.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.handler.HandlerResponse;
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

    public static ModelAndView of(HandlerResponse handlerResponse) throws IOException {
        String body = handlerResponse.getBody();
        final URL resource = ModelAndView.class.getClassLoader().getResource("static" + body);
        if (resource == null) {
            return new ModelAndView(handlerResponse.getHttpStatus(), body, ContentType.HTML);
        }

        File file = new File(resource.getFile());
        String view = new String(Files.readAllBytes(file.toPath()));
        ContentType contentType = ContentType.of(getFileType(body));

        return new ModelAndView(handlerResponse.getHttpStatus(), view, contentType);
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
