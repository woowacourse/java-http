package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequestHeader;

public class HttpResponse {

    private static final String STATIC_FILE_PATH = "static";
    private static final String FILE_DELIMITER = ".";

    private final String response;

    public HttpResponse(HttpRequestHeader httpRequestHeader) throws IOException {
        response = getResponse(httpRequestHeader);
    }

    private String getResponse(HttpRequestHeader httpRequestHeader) throws IOException {
        final String body = getBody(httpRequestHeader);
        final String header = getHeader(httpRequestHeader, body);
        return String.join("\r\n", header, body);
    }

    private String getBody(HttpRequestHeader httpRequestHeader) throws IOException {
        String requestUri = httpRequestHeader.getRequestUri();
        if (!requestUri.contains(FILE_DELIMITER)) {
            requestUri += ".html";
        }
        final URL resource = getClass().getClassLoader().getResource(STATIC_FILE_PATH + requestUri);
        if (resource == null) {
            return "Hello world!";
        }
        final Path path = new File(resource.getPath()).toPath();
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }

    private String getHeader(HttpRequestHeader httpRequestHeader, String body) {
        String responseLine = "HTTP/1.1 200 OK ";
        String contentType = getContentType(httpRequestHeader);
        String contentLength = body.getBytes().length + " ";

        return String.join("\r\n",
                responseLine,
                "Content-Type: " + contentType,
                "Content-Length: " + contentLength,
                "");
    }

    private String getContentType(HttpRequestHeader httpRequestHeader) {
        String requestUri = httpRequestHeader.getRequestUri();
        return ContentType.find(requestUri) + ";charset=utf-8 ";
    }

    public byte[] getBytes() {
        return response.getBytes();
    }

    public String getResponse() {
        return response;
    }
}
