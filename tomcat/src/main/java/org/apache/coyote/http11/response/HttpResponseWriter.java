package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.Headers;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponseWriter {
    private static final String CSRF = "\r\n";

    public static void write(final OutputStream out, final HttpResponse response) throws IOException {
        writeStatusLine(out, response);
        writeHeaders(out, response.getHeaders());
        writeCSRF(out);
        writeBody(out, response.getBody());
    }

    private static void writeStatusLine(final OutputStream out, final HttpResponse response) throws IOException {
        writeStringWithCSRF(out, response.getStatusLine());
    }

    private static void writeHeaders(final OutputStream out, final Headers headers) throws IOException {
        for (final String line : headers.formats()) {
            writeStringWithCSRF(out, line);
        }
    }

    private static void writeBody(final OutputStream out, final byte[] body) throws IOException {
        out.write(body);
    }

    private static void writeStringWithCSRF(final OutputStream out, final String str) throws IOException {
        out.write(str.getBytes());
        writeCSRF(out);
    }

    private static void writeCSRF(final OutputStream out) throws IOException {
        out.write((CSRF).getBytes());
    }

    private HttpResponseWriter() {}
}
