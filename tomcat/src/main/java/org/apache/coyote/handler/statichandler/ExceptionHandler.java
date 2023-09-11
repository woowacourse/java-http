package org.apache.coyote.handler.statichandler;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ExceptionHandler implements Handler {

    private static final String DEFAULT_DIRECTORY_PATH = "static";
    private static final String PATH_DELIMITER = "/";

    private final HttpStatus httpStatus;

    public ExceptionHandler(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            String body = findErrorPage();

            httpResponse.setStatusLine(httpRequest.httpVersion(), httpStatus);
            httpResponse.setHeader(HttpHeader.CONTENT_TYPE.value(), ContentType.TEXT_HTML.value());
            httpResponse.setHeader(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(body.getBytes().length));
            httpResponse.setBody(body);
        } catch (Exception e) {
            new ExceptionHandler(HttpStatus.NOT_FOUND).service(httpRequest, httpResponse);
        }
    }

    private String findErrorPage() throws IOException {
        String path = DEFAULT_DIRECTORY_PATH + PATH_DELIMITER + httpStatus.statusCode() + ContentType.TEXT_HTML.extension();
        File file = new File(HttpResponse.class
                .getClassLoader()
                .getResource(path)
                .getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
