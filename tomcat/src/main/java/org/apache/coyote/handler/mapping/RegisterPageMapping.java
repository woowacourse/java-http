package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class RegisterPageMapping implements HandlerMapping {

    @Override
    public boolean supports(final HttpMethod httpMethod, final String requestUri) {
        return HttpMethod.GET == httpMethod &&
                requestUri.contains("register");
    }

    @Override
    public String handle(final String requestUri, final Map<String, String> headers, final String requestBody) throws IOException {
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
