package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegisterPageMapping implements HandlerMapping {

    public static final String TARGET_URI = "register";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() && httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    public String handle(final HttpRequest httpRequest) throws IOException {
        final String filePath = "static/register.html";
        final URL fileUrl = getClass().getClassLoader().getResource(filePath);
        final Path path = new File(fileUrl.getPath()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
