package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private final HttpRequest httpRequest;
    private final HttpCookie httpCookie;
    private final String responseBody;

    private HttpResponse(HttpRequest httpRequest, HttpCookie httpCookie, String responseBody) {
        this.httpRequest = httpRequest;
        this.httpCookie = httpCookie;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(HttpRequest httpRequest) throws IOException {
        String responseBody = loadBody(httpRequest);
        return new HttpResponse(httpRequest, httpRequest.getCookie(), responseBody);
    }

    public static HttpResponse from(HttpRequest httpRequest, HttpCookie httpCookie) throws IOException {
        String responseBody = loadBody(httpRequest);
        return new HttpResponse(httpRequest, httpCookie, responseBody);
    }

    private static String loadBody(HttpRequest httpRequest) throws IOException {
        String path = httpRequest.getPath();
        URL resource = HttpResponse.class.getClassLoader().getResource("static" + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public static HttpResponse empty(HttpRequest httpRequest) {
        return new HttpResponse(httpRequest, httpRequest.getCookie(), "Hello world!");
    }

    public String ok() {
        String response = build(HttpStatusCode.OK);
        return String.join("", response, "\r\n\r\n", responseBody);
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
        String setCookieHeader = "Set-Cookie: " + httpCookie.toCookieLine();
        return String.join("\r\n",
                responseLine,
                contentTypeHeader,
                contentLengthHeader,
                setCookieHeader
        );
    }
}
