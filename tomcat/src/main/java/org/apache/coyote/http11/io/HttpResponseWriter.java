package org.apache.coyote.http11.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class HttpResponseWriter {
    private final OutputStreamWriter outputStreamWriter;

    public HttpResponseWriter(OutputStreamWriter outputStreamWriter) {
        this.outputStreamWriter = outputStreamWriter;
    }

    public void writeResponse(HttpResponse httpResponse) throws IOException {
        writeStatusLine(httpResponse.getStatusLine());
        writeResponseHeader(httpResponse.getResponseHeader());
        outputStreamWriter.write("\r\n");
        outputStreamWriter.write(httpResponse.getResponseBody());
    }

    private void writeStatusLine(StatusLine statusLine) throws IOException {
        outputStreamWriter.write(String.format("%s %d %s \r\n",
                statusLine.getVersion(),
                statusLine.getStatusCode(),
                statusLine.getStatusMessage()));
    }

    private void writeResponseHeader(Map<String, String> responseHeader) throws IOException {
        for (String key : responseHeader.keySet()) {
            outputStreamWriter.write(String.format("%s: %s \r\n", key, responseHeader.get(key)));
        }
    }
}
