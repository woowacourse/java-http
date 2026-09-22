package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponseWriter {

    public void write(
            OutputStream outputStream,
            HttpResponse response
    ) throws IOException {
        writeStatusLine(outputStream, response);
        writeHeaders(outputStream, response);

        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write(response.getBody());
        outputStream.flush();
    }

    private void writeStatusLine(
            OutputStream outputStream,
            HttpResponse response
    ) throws IOException {
        String statusLine = response.getStatusLine() + "\r\n";

        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
    }

    private void writeHeaders(
            OutputStream outputStream,
            HttpResponse response
    ) throws IOException {
        Map<String, String> headers = response.getHeader().getValues();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Content-Length")) {
                continue;
            }

            String headerLine = entry.getKey()
                    + ": " + entry.getValue()
                    + "\r\n";

            outputStream.write(headerLine.getBytes(StandardCharsets.UTF_8));
        }

        String contentLength = "Content-Length: "
                + response.getContentLength()
                + "\r\n";

        outputStream.write(contentLength.getBytes(StandardCharsets.UTF_8));
    }
}
