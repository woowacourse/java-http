package org.apache.controller;

import org.apache.http.HttpRequestMessage;

public class RootController implements Controller {

    private final String processableUri = "/";

    @Override
    public boolean isProcessableRequest(HttpRequestMessage request) {
        return processableUri.equals(request.getUri());
    }

    @Override
    public String processRequest(HttpRequestMessage request) {
        String responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
