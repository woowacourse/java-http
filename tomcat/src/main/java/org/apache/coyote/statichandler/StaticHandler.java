package org.apache.coyote.statichandler;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.dynamichandler.AbstractHandler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticHandler extends AbstractHandler {

    private static final String DEFAULT_DIRECTORY_PATH = "static";

    @Override
    public void doGet(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        try {
            String path = httpRequest.getPath();
            String body = findPage(path);

            httpResponse.setStatus(OK);
            httpResponse.setHeader(CONTENT_TYPE.getValue(), ContentType.findByExtension(path).getValue());
            httpResponse.setHeader(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
            httpResponse.setResponseBody(body);
        } catch (IOException e) {
            new ExceptionHandler(HttpStatus.NOT_FOUND).service(httpRequest, httpResponse);
        }
    }

    public String findPage(String path) throws IOException {
        File file = new File(getClass()
                .getClassLoader()
                .getResource(DEFAULT_DIRECTORY_PATH + path)
                .getFile()
        );

        return new String(Files.readAllBytes(file.toPath()));
    }

    @Override
    public void doPost(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        new ExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR).service(httpRequest, httpResponse);
    }

}
