package org.apache.coyote.http11.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

public final class ErrorResponder {

    private ErrorResponder() {}

    public static void send500(OutputStream outputStream) throws IOException {
        String body = StaticResourceResolver.readAsString("500.html");
        if (body == null) {
            body = "<h1>500 Internal Server Error</h1>";
        }
        String response = HttpResponseWriter.serverError(body, Collections.emptyMap());
        HttpResponseWriter.write(outputStream, response);
    }

    public static void send404(OutputStream outputStream) throws IOException {
        String body = StaticResourceResolver.readAsString("404.html");
        if (body == null) {
            body = "<h1>404 Not Found</h1>";
        }
        String response = HttpResponseWriter.notFound(body, Collections.emptyMap());
        HttpResponseWriter.write(outputStream, response);
    }
}
