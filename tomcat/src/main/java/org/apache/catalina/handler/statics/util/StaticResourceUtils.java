package org.apache.catalina.handler.statics.util;

import java.io.IOException;
import org.apache.catalina.controller.util.StaticResourceReader;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.response.HttpResponse;

public final class StaticResourceUtils {

    private static final String BASE_PATH = "static/";

    private StaticResourceUtils() {
    }

    public static void serve(HttpResponse response, String filePath, HttpStatus status) throws IOException {
        String resourcePath = BASE_PATH + filePath;
        byte[] bytes = StaticResourceReader.readResource(resourcePath);
        String contentType = StaticResourceReader.resolveContentType(filePath);

        response.status(status)
                .contentType(contentType)
                .write(bytes);
    }
}
