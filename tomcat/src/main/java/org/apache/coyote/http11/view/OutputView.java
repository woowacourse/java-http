package org.apache.coyote.http11.view;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.apache.coyote.http11.dto.HttpResponseDto;

public class OutputView {

    private static final String STATUS_LINE = "HTTP/1.1 %d %s";
    private static final String HEADER_LINE = "%s: %s";

    private final OutputStream outputStream;

    public OutputView(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(HttpResponseDto httpResponse) throws IOException {
        writeStatusLine(httpResponse.httpStatusCode(), httpResponse.reasonPhrase());
        writeHeaders(httpResponse.responseHeaders());
        writeNewLine();
        writeMessageBody(httpResponse.messageBody());
        outputStream.flush();
    }

    private void writeStatusLine(int httpStatusCode, String reasonPhrase) throws IOException {
        String statusLine = String.format(STATUS_LINE, httpStatusCode, reasonPhrase);
        outputStream.write(statusLine.getBytes());
        writeNewLine();
    }

    private void writeHeaders(Map<String, String> responseHeaders) throws IOException {
        for (String key : responseHeaders.keySet()) {
            writeHeader(key, responseHeaders.get(key));
        }
    }

    private void writeHeader(String key, String value) throws IOException {
        String headerLine = String.format(HEADER_LINE, key, value);
        outputStream.write(headerLine.getBytes());
        writeNewLine();
    }

    private void writeNewLine() throws IOException {
        outputStream.write("\r\n".getBytes());
    }

    private void writeMessageBody(String messageBody) throws IOException {
        if (messageBody == null) {
            return;
        }

        outputStream.write(messageBody.getBytes());
    }
}
