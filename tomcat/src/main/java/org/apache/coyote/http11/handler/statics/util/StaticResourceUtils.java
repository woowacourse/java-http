package org.apache.coyote.http11.handler.statics.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.response.HttpResponse;

public final class StaticResourceUtils {

    private static final String BASE_PATH = "static/";

    private StaticResourceUtils() {
    }

    public static void serve(HttpResponse response, String filePath, HttpStatus status) throws IOException {
        try (InputStream inputStream = StaticResourceUtils.class.getClassLoader().getResourceAsStream(BASE_PATH + filePath)) {
            if (inputStream == null) {
                response.status(HttpStatus.NOT_FOUND.getCode(), HttpStatus.NOT_FOUND.getReason())
                        .contentType("text/plain")
                        .write("Not Found");
                return;
            }
            byte[] bytes = inputStream.readAllBytes();
            response.status(status.getCode(), status.getReason())
                    .contentType("text/html")
                    .write(bytes);
        }
    }
}
