package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private final HttpRequest httpRequest;
    private final String responseBody;

    private HttpResponse(HttpRequest httpRequest, String responseBody) {
        this.httpRequest = httpRequest;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(HttpRequest httpRequest) throws IOException {
        String responseBody = loadBody(httpRequest);
        return new HttpResponse(httpRequest, responseBody);
    }

    private static String loadBody(HttpRequest httpRequest) throws IOException {
        String path = httpRequest.getPath();
        ContentType contentType = httpRequest.getContentType();
        if (!path.contains(".")) {
            path += "." + contentType;
        }
        URL resource = HttpResponse.class.getClassLoader().getResource("static" + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public static HttpResponse empty(HttpRequest httpRequest) {
        return new HttpResponse(httpRequest, "Hello world!");
    }

    public String ok() throws IOException {
        String response = build(HttpStatusCode.OK);
        return String.join(response, "\r\n\r\n", responseBody);
    }

    public String notFound() {
        String response = build(HttpStatusCode.NOT_FOUND);
        return String.join(response, "\r\n\r\n", responseBody);
    }

    public String found(String location) {
        String response = build(HttpStatusCode.FOUND);
        return String.join(
                "\r\n",
                response,
                "Location: " + location,
                "\r\n"
        );
    }

    private String build(HttpStatusCode httpStatusCode) {
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
}
