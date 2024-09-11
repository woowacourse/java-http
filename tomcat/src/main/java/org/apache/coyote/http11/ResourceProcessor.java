package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import util.ResourceFileLoader;

public class ResourceProcessor {

    public void process(Socket connection, String requestPath) throws IOException {
        final var outputStream = connection.getOutputStream();

        String contentType = "";
        String extension = requestPath.split("\\.")[1];
        if (extension.equals("html")) {
            contentType = "text/html";
        }
        if (extension.equals("css")) {
            contentType = "text/css";
        }
        if (extension.equals("js")) {
            contentType = "text/javascript";
        }

        String responseBody = ResourceFileLoader.loadFileToString("static" + requestPath);
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
