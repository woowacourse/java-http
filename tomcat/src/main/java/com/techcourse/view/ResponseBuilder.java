package com.techcourse.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.exception.TomcatException;
import org.apache.coyote.http11.response.HttpStatusCode;

public class ResponseBuilder {

    public String buildExceptionResponse(TomcatException e) throws IOException {
        HttpStatusCode httpStatusCode = e.getHttpStatusCode();
        int statusCode = httpStatusCode.getStatusCode();

        URL url = getClass().getClassLoader().getResource("static/" + statusCode + ".html");
        String responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));

        return String.join(System.lineSeparator(),
                "HTTP/1.1 " + statusCode + " " + httpStatusCode.getStatusMessage(),
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);
    }
}
