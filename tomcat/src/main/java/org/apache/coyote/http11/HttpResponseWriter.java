package org.apache.coyote.http11;

import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.response.HttpResponse;
import util.StringUtil;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponseWriter {
    private static final String CSRF = "\r\n";
    private static final String LINE_SUFFIX = " ";

    public static void write(final OutputStream out, final HttpResponse response) throws IOException {
        writeStatusLine(out, response);
        writeHeaders(out, response.getHeaders());
        writeCSRF(out);
        writeBody(out, response.getBody());
        out.flush();
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
        out.write(StringUtil.addSuffixIfNotEndSuffix(str,LINE_SUFFIX).getBytes());
        writeCSRF(out);
    }

    private static void writeCSRF(final OutputStream out) throws IOException {
        out.write((CSRF).getBytes());
    }

    private HttpResponseWriter() {}
}
