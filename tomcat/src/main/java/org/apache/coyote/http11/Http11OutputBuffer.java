package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

public class Http11OutputBuffer implements AutoCloseable {

    private final OutputStream outputStream;

    public Http11OutputBuffer(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void commitAndWrite(
            HttpResponse httpResponse
    ) throws IOException {
        if (httpResponse.isCommitted()) {
            return;
        }

        byte[] body = httpResponse.getBody();
        writeContentLength(httpResponse, body);
        writeLine("HTTP/1.1 " + httpResponse.getStatus() + " " + httpResponse.getReason() + " ");
        for (Entry<String, String> h : httpResponse.getHeaders().entrySet()) {
            writeLine(h.getKey() + ": " + h.getValue() + " ");
        }
        writeCRLF();

        httpResponse.markCommitted();

        if (body.length > 0) {
            outputStream.write(body);
        }
        outputStream.flush();
    }

    private void writeContentLength(
            HttpResponse httpResponse,
            byte[] body
    ) {
        if (!httpResponse.getHeaders().containsKey("Content-Length")) {
            httpResponse.setHeader("Content-Length", String.valueOf(body.length));
        }
    }

    private void writeLine(String string) throws IOException {
        outputStream.write(string.getBytes(StandardCharsets.US_ASCII));
        writeCRLF();
    }

    private void writeCRLF() throws IOException {
        outputStream.write('\r');
        outputStream.write('\n');
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
