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

    private String build() {
        String responseLine = request.getProtocolVersion() + " " + statusCode.getStatus();
        ContentType contentType = request.getContentType();
        String contentTypeHeader = "Content-Type: text/" + contentType.getName() + ";charset=utf-8";
        String contentLengthHeader = "Content-Length: " + responseBody.getBytes().length;
        String setCookieHeader = "Set-Cookie: JSESSIONID=" + request.getJSessionId();
        return String.join("\r\n",
                responseLine,
                contentTypeHeader,
                contentLengthHeader,
                setCookieHeader
        );
    }

    public void redirect(OutputStream outputStream, String redirectUrl) throws IOException {
        final var response = String.join(
                "\r\n",
                build(),
                "Location: " + redirectUrl,
                "\r\n"
        );
        System.out.println(response);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public void respond(OutputStream outputStream) throws IOException {
        final var response = build() + "\r\n\r\n" + responseBody;

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
