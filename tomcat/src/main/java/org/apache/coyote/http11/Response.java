package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

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
        ContentType contentType = request.getContentType();
        if (!path.contains(".")) {
            path += "." + contentType;
        }
        final URL resource = classLoader.getResource("static" + path);
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return new Response(request, statusCode, responseBody);
    }

    public static void redirect(OutputStream outputStream, String redirectUrl) throws IOException {
        redirect(outputStream, redirectUrl, "");
    }

    public static void redirect(OutputStream outputStream, String redirectUrl, String jSessionId) throws IOException {
        String setCookieHeader = "";
        if (!jSessionId.isEmpty()) {
            setCookieHeader = "Set-Cookie: JSESSIONID=" + jSessionId + "\r\n";
        }
        final var response = "HTTP/1.1 " + StatusCode.FOUND.getStatusCode() + "\r\n"
                + "Location: " + redirectUrl + "\r\n"
                + setCookieHeader
                + "Content-Length: 0\r\n"
                + "\r\n";
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public void respond(OutputStream outputStream) throws IOException {
        ContentType contentType = request.getContentType();
        final var response = String.join("\r\n",
                "HTTP/1.1 " + statusCode.getStatusCode(),
                "Content-Type: text/" + contentType.getName() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
