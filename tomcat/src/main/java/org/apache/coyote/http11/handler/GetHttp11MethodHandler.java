package org.apache.coyote.http11.handler;

import static org.apache.coyote.HttpMethod.GET;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.util.RequestExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetHttp11MethodHandler implements Http11MethodHandler {

    private static final Logger log = LoggerFactory.getLogger(GetHttp11MethodHandler.class);

    @Override
    public HttpMethod supportMethod() {
        return GET;
    }

    @Override
    public String handle(final String request) {
        String targetPath = RequestExtractor.extractTargetPath(request);

        if (targetPath.equals("/")) {
            return defaultContent();
        }
        return resourceContent(targetPath);
    }

    private String defaultContent() {
        final var responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String resourceContent(final String targetPath) {
        URL resourcePath = getClass().getClassLoader().getResource("static" + targetPath);

        String responseBody = null;
        try {
            responseBody = new String(Files.readAllBytes(new File(resourcePath.getFile()).toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
