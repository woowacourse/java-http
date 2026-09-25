package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

public class Response {

    private final Request request;
    private final StatusCode statusCode;
    private final String responseBody;

    public Response(Request request, StatusCode statusCode, String responseBody) {
        this.request = request;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public static Response empty(Request request) {
        return new Response(request, StatusCode.OK, "Hello world!");
    }

    public static Response from(Request request, StatusCode statusCode, ClassLoader classLoader) throws IOException {
        String path = request.getPath();
        String contentType = request.getContentTypeName();
        if (!path.contains(".")) {
            path += "." + contentType;
        }
        final URL resource = classLoader.getResource("static" + path);
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return new Response(request, statusCode, responseBody);
    }

    public static void redirect(OutputStream outputStream, String redirectUrl) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 " + StatusCode.FOUND.getStatusCode(),
                "Location: " + redirectUrl,
                "Content-Length: 0",
                "",
                ""
        );
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public void response(OutputStream outputStream) throws IOException {
        String jSessionId = request.getJSessionId();
        if (!request.hasJSessionId()) {
            jSessionId = UUID.randomUUID().toString();
        }
        final var response = String.join("\r\n",
                "HTTP/1.1 " + statusCode.getStatusCode(),
                "Set-Cookie: JSESSIONID=" + jSessionId,
                "Content-Type: text/" + request.getContentTypeName() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
