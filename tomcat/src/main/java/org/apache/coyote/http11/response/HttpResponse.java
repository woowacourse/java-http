package org.apache.coyote.http11.response;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpResponse {

    private String statusLine;
    private List<String> headers = new ArrayList<>();
    private String body;

    public HttpResponse() {}

    public void addStatusLine(String status) {
        this.statusLine = "HTTP/1.1 " + status + " ";
    }

    public void addContentTypeHeader(String contentType) {
        this.headers.add("Content-Type: " + contentType + ";charset=utf-8 ");
    }

    public void addBody(String body) {
        this.headers.add("Content-Length: " + body.getBytes().length + " ");
        this.body = body;
    }

    public void addBodyFromFile(String fileName) {
        String body = readFile("static" + fileName);
        this.headers.add("Content-Length: " + body.getBytes().length + " ");
        this.body = body;
    }

    private String readFile(String fileName) {
        try {
            URL resource = getClass().getClassLoader().getResource(fileName);
            final Path path = Path.of(Objects.requireNonNull(resource).getPath());

            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    public void addHeader(String header) {
        this.headers.add(header);
    }

    public String makeResponse() {
        return String.join("\r\n",
                this.statusLine,
                String.join("\r\n", this.headers),
                "",
                this.body);
    }
}
