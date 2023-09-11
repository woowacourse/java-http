package org.apache.coyote.statichandler;

import static org.apache.coyote.http11.ContentType.HTML;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

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

            httpResponse.setStatus(httpStatus);
            httpResponse.setHeader(HttpHeader.CONTENT_TYPE.getValue(), HTML.getValue());
            httpResponse.setHeader(HttpHeader.CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
            httpResponse.setResponseBody(body);
        } catch (IOException e) {
            new ExceptionHandler(HttpStatus.NOT_FOUND).service(httpRequest, httpResponse);
        }
    }

    public String findErrorPage() throws IOException {
        String path = DEFAULT_DIRECTORY_PATH + PATH_DELIMITER + httpStatus.getValue() + HTML.getExtension();

        File file = new File(getClass()
                .getClassLoader()
                .getResource(path)
                .getFile()
        );

        return new String(Files.readAllBytes(file.toPath()));
    }

}
