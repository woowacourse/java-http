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

public class StaticHandler implements Handler {

    private static final String DEFAULT_DIRECTORY_PATH = "static";

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            String body = findStaticPage(DEFAULT_DIRECTORY_PATH + httpRequest.path());

            httpResponse.setStatusLine(httpRequest.httpVersion(), HttpStatus.OK);
            httpResponse.setHeader(HttpHeader.CONTENT_TYPE.value(), ContentType.findContentTypeByFilename(httpRequest.path()).value());
            httpResponse.setHeader(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(body.getBytes().length));
            httpResponse.setBody(body);
        } catch (Exception e) {
            new ExceptionHandler(HttpStatus.NOT_FOUND).service(httpRequest, httpResponse);
        }
    }

    private String findStaticPage(String path) throws IOException {
        File file = new File(HttpResponse.class
                .getClassLoader()
                .getResource(path)
                .getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
