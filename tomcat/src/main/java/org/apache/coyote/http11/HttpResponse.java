package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private static final String STATIC_DIRECTORY = "static";
    private static final String DELIMITER = "\r\n";

    private final StatusCode statusCode;
    private final String contentType;
    private final String responseBody;

    public HttpResponse(final StatusCode statusCode, final String contentType, final String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public byte[] toBytes() {
        return String.join(DELIMITER,
                "HTTP/1.1 " + statusCode.getValue() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody).getBytes();
    }

    public static HttpResponse toNotFound() {
        URL resource = classLoader.getResource(STATIC_DIRECTORY + "/404.html");
        final File file = new File(resource.getPath());
        try{
            return new HttpResponse(StatusCode.NOT_FOUND, "text/html", new String(Files.readAllBytes(file.toPath())));
        } catch (IOException e) {
            e.printStackTrace();
            return toNotFound();
        }
    }

    public static HttpResponse toUnauthorized() {
        URL resource = classLoader.getResource(STATIC_DIRECTORY + "/401.html");
        final File file = new File(resource.getPath());
        try{
            return new HttpResponse(StatusCode.UNAUTHORIZED, "text/html", new String(Files.readAllBytes(file.toPath())));
        } catch (IOException e) {
            e.printStackTrace();
            return toNotFound();
        }
    }
}
