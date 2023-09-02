package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Response {
    private static final int URI_INDEX = 1;

    private Type type;
    private String content;
    private String response;

    public Response(final BufferedReader bufferedReader) throws IOException {
        final String request = bufferedReader.readLine();
        final String requestUri = request.split(" ")[URI_INDEX];
        this.content = getFileContent(requestUri);
    }

    public Response type(final Type type) {
        this.type = type;
        return this;
    }

    public Response content(final String content) {
        this.content = content;
        return this;
    }

    public Response response(final String response) {
        this.response = response;
        return this;
    }

    public String build() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + type.getValue() + ";charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
    }

    private String getFileContent(String fileName) throws IOException {
        try {
            if (fileName.equals("/")) {
                fileName = "/index.html";
            } else if (fileName.startsWith("/login")) {
                // http://localhost:8080/login?account=gugu&password=password
                fileName = "/login.html";
            }
            final URL resource = getClass().getClassLoader().getResource("static" + fileName);
            final Path path = Paths.get(resource.getPath());
            return Files.readString(path);
        } catch (final NullPointerException e) {
            return "";
        }
    }
}
