package org.apache.coyote.http11.util;

import java.io.IOException;
import java.io.OutputStream;

public final class ErrorResponder {

    private ErrorResponder() {}

    public static void send500(OutputStream outputStream) throws IOException {
        String body = StaticResourceResolver.read("500.html");
        if (body == null) {
            body = "<h1>500 Internal Server Error</h1>";
        }
        String response = HttpResponseWriter.serverError(body);
        HttpResponseWriter.write(outputStream, response);
    }

    public static void send404(OutputStream outputStream) throws IOException {
        String body = StaticResourceResolver.read("404.html");
        if (body == null) {
            body = "<h1>404 Not Found</h1>";
        }
        String response = HttpResponseWriter.notFound(body);
        HttpResponseWriter.write(outputStream, response);
    }
}
