package org.apache.coyote.http.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponseWriter {

    public void write(HttpResponse response, OutputStream outputStream) throws IOException {
        StringBuilder responseText = new StringBuilder();
        responseText.append(response.getResponseLine()).append("\r\n");

        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            responseText.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        responseText.append("\r\n");
        responseText.append(response.getBody());

        outputStream.write(responseText.toString().getBytes());
        outputStream.flush();
    }
}