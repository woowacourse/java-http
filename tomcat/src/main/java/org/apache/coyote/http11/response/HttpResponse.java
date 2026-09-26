package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private final HttpRequest httpRequest;
    private final HttpStatusCode httpStatusCode;
    private final String responseBody;

    public HttpResponse(HttpRequest httpRequest, HttpStatusCode httpStatusCode, String responseBody) {
        this.httpRequest = httpRequest;
        this.httpStatusCode = httpStatusCode;
        this.responseBody = responseBody;
    }

    public static HttpResponse empty(HttpRequest httpRequest) {
        return new HttpResponse(httpRequest, HttpStatusCode.OK, "Hello world!");
    }

    public static HttpResponse from(HttpRequest httpRequest, HttpStatusCode httpStatusCode, ClassLoader classLoader)
            throws IOException {
        String path = httpRequest.getPath();
        ContentType contentType = httpRequest.getContentType();
        if (!path.contains(".")) {
            path += "." + contentType;
        }
        final URL resource = classLoader.getResource("static" + path);
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return new HttpResponse(httpRequest, httpStatusCode, responseBody);
    }

    private String build() {
        String responseLine = httpRequest.getProtocolVersion() + " " + httpStatusCode.getStatus();
        ContentType contentType = httpRequest.getContentType();
        String contentTypeHeader = "Content-Type: text/" + contentType.getName() + ";charset=utf-8";
        String contentLengthHeader = "Content-Length: " + responseBody.getBytes().length;
        String setCookieHeader = "Set-Cookie: JSESSIONID=" + httpRequest.getJSessionId();
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
