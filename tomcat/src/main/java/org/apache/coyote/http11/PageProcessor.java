package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import util.ResourceFileLoader;

public class PageProcessor {


    public void process(OutputStream outputStream, String resourceName) throws IOException {
        String contentType = "text/html";
        String responseBody = ResourceFileLoader.loadFileToString("static/" + resourceName + ".html");
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public void processWithHttpStatus(OutputStream outputStream, String resourceName, HttpStatus httpStatus)
            throws IOException {
        String contentType = "text/html";
        String responseBody = ResourceFileLoader.loadFileToString("static/" + resourceName + ".html");
        final var response = String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getHeaderForm(),
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
