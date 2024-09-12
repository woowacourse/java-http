package org.apache.coyote.http11.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.message.HttpResponseMessage;

public class HttpResponseWriter {

    private static final String STATUS_LINE = "HTTP/1.1 %d %s";
    private static final String HEADER_LINE = "%s: %s";

    private final OutputStreamWriter outputStreamWriter;

    public HttpResponseWriter(OutputStreamWriter outputStreamWriter) {
        this.outputStreamWriter = outputStreamWriter;
    }

    public void write(HttpResponseMessage httpResponse) throws IOException {
        writeStatusLine(httpResponse.httpStatusCode(), httpResponse.reasonPhrase());
        writeHeaders(httpResponse.responseHeaders());
        writeNewLine();
        writeMessageBody(httpResponse.messageBody());
        outputStreamWriter.flush();
    }

    private void writeStatusLine(int httpStatusCode, String reasonPhrase) throws IOException {
        String statusLine = String.format(STATUS_LINE, httpStatusCode, reasonPhrase);
        outputStreamWriter.write(statusLine);
        writeNewLine();
    }

    private void writeHeaders(Map<String, String> responseHeaders) throws IOException {
        for (String key : responseHeaders.keySet()) {
            writeHeader(key, responseHeaders.get(key));
        }
    }

    private void writeHeader(String key, String value) throws IOException {
        String headerLine = String.format(HEADER_LINE, key, value);
        outputStreamWriter.write(headerLine);
        writeNewLine();
    }

    private void writeNewLine() throws IOException {
        outputStreamWriter.write(StringUtils.CR);
        outputStreamWriter.write(StringUtils.LF);
    }

    private void writeMessageBody(String messageBody) throws IOException {
        if (messageBody == null) {
            return;
        }

        outputStreamWriter.write(messageBody);
    }
}
