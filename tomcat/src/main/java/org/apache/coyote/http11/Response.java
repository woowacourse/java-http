package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.apache.coyote.http11.ContentType.*;

public class Response {

    private final String response;

    public Response(ContentType contentType, String resource) throws IOException {
        this.response = makeResponse(contentType, resource);
    }

    private String makeResponse(ContentType contentType, String resource) throws IOException {
        String body = getBody(resource);
        String typeValue = contentType.getValue();
        if (contentType.equals(TEXT_CSS) || contentType.equals(TEXT_HTML)) {
            typeValue += ";charset=utf-8";
        }
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + typeValue + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private static String getBody(String resource) throws IOException {
        if (resource.equals("/")) {
            return "Hello world!";
        }
        RequestUrl requestUrl = new RequestUrl(resource);
        URL url = requestUrl.getUrl();

        Path path = new File(Objects.requireNonNull(url).getFile()).toPath();
        return new String(Files.readAllBytes(path));
    }

    public String getResponse() {
        return response;
    }
}
