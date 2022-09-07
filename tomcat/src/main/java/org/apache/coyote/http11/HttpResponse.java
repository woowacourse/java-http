package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class HttpResponse {

    private final String response;

    public HttpResponse(String statusCode, String contentType, String responseBody) {
        this.response = String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public static HttpResponse of(String statusCode, String file) throws IOException {
        final String responseBody = readFile("static" + file);
        return new HttpResponse(statusCode, ContentType.findContentType(file), responseBody);
    }

    private static String readFile(String fileName) throws IOException {
        URL resource = HttpResponse.class.getClassLoader().getResource(fileName);
        final Path path = Path.of(Objects.requireNonNull(resource).getPath());

        return Files.readString(path);
    }

    public String getResponse() {
        return response;
    }
}
