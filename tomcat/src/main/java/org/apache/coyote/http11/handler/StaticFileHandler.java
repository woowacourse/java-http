package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.exception.NotExistFileException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.coyote.http11.common.HttpStatus.FOUND;
import static org.apache.coyote.http11.common.HttpStatus.OK;

public class StaticFileHandler {

    private StaticFileHandler() {
    }

    public static HttpResponse handle(final String requestURI, final HttpRequest request) {
        RequestHeader requestHeader = request.getRequestHeader();
        try {
            String responseBody = findResponseBody(requestURI);
            if (requestURI.endsWith("html")) {
                return new HttpResponseBuilder().init()
                        .httpStatus(OK)
                        .header("Content-Type: text/html;charset=utf-8 ")
                        .header("Content-Length: " + responseBody.getBytes().length + " ")
                        .body(responseBody)
                        .build();
            }
            return new HttpResponseBuilder().init()
                    .httpStatus(OK)
                    .header("Content-Type: " + requestHeader.getHeaderValue("Accept").split(",")[0] + ";charset=utf-8 ")
                    .header("Content-Length: " + responseBody.getBytes().length + " ")
                    .body(responseBody)
                    .build();
        } catch (IOException | NullPointerException e) {
            return new HttpResponseBuilder().init()
                    .httpStatus(FOUND)
                    .header("Location: /404.html ")
                    .build();
        }
    }

    private static String findResponseBody(final String requestURI) throws IOException {
        try {
            URL requestedFile = ClassLoader.getSystemClassLoader().getResource("static" + requestURI);
            return new String(Files.readAllBytes(new File(requestedFile.getFile()).toPath()));
        } catch (NullPointerException e) {
            throw new NotExistFileException();
        }
    }
}
