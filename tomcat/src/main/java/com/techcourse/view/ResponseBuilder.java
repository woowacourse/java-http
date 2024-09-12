package com.techcourse.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

class ResponseBuilder {

    String buildSuccessfulResponse(String responseBody) {
        return this.buildSuccessfulResponse("text/html", responseBody);
    }

    String buildSuccessfulResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    String buildNotFoundResponse() throws IOException {
        URL badRequestURL = getClass().getClassLoader().getResource("static/404.html");
        String responseBody = new String(Files.readAllBytes(new File(badRequestURL.getFile()).toPath()));

        return String.join("\r\n",
                "HTTP/1.1 404 NOT_FOUND ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    String buildRedirectResponse(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + location);
    }
}
