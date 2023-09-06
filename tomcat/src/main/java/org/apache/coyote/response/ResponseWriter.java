package org.apache.coyote.response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.request.Request;

public class ResponseWriter {

    private final OutputStream outputStream;

    public ResponseWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeResponse(Request request) throws URISyntaxException, IOException {
//        String responseBody = readResponseBody(request.getUrl());
//        String response = getResponse(request, responseBody);

//        outputStream.write(response.getBytes());
//        outputStream.flush();
    }

    private String readResponseBody(URL resource) throws URISyntaxException, IOException {
        Path resourcePath = Path.of(resource.toURI());
        return new String(Files.readAllBytes(resourcePath));
    }

    private String getResponse(Request request, String responseBody) {
        String responseHeader = getResponseHeader(request, responseBody);

        return String.join("\r\n",
                responseHeader,
                responseBody);
    }

    private String getResponseHeader(Request request, String responseBody) {
        String status = "200 OK ";
//        if (!request.isExists()) {
//            status = "404 NOT_FOUND ";
//        }

        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + request.getResourceTypes() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "");
    }
}
