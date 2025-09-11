package org.apache.coyote.http11.util;

import static com.techcourse.HttpStaus.OK;

import com.techcourse.HttpStaus;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.response.HttpResponse;

public class ResponseHandler {

    public static void sendDefaultResource(HttpResponse response) throws IOException {
        final var responseBody = "Hello world!";
        response.setStatusCode(OK.getValue())
                .setContentType("text/html;charset=utf-8")
                .setBody(responseBody)
                .send();
    }

    public static void redirect(HttpResponse response, String location, HttpStaus statusCode) throws IOException {
        response.setStatusCode(statusCode.getValue())
                .setLocation(location)
                .send();
    }

    public static void sendStaticFile(HttpResponse response, String url, HttpStaus statusCode)
            throws IOException, URISyntaxException {
        final var contentType = ResourceLoader.getResourceContentType(url);
        final byte[] responseBodyBytes = ResourceLoader.getResourceBytes(url);

        response.setStatusCode(statusCode.getValue())
                .setContentType(contentType)
                .setBody(responseBodyBytes)
                .send();
    }
}
