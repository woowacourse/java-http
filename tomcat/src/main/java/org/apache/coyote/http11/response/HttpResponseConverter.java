package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.Map.Entry;

public class HttpResponseConverter {

    private static final String CRLF = "\r\n";

    public byte[] convert(HttpResponse response) {
        StringBuilder stringBuilder = new StringBuilder();
        convertStatusLine(stringBuilder, response.getStatusLine());
        convertHeader(stringBuilder, response.getHeader());
        convertBody(stringBuilder, response.getBody());

        return stringBuilder.toString().getBytes();
    }

    private void convertStatusLine(StringBuilder stringBuilder, StatusLine statusLine) {
        String statusLineFormat = String.format("%s %s %s ",
                statusLine.getVersion(), statusLine.getStatusCode(), statusLine.getStatusMessage());
        stringBuilder.append(statusLineFormat).append(CRLF);
    }

    private void convertHeader(StringBuilder stringBuilder, ResponseHeader header) {
        Map<String, String> headers = header.getHeaders();
        for (Entry<String, String> headerEntry : headers.entrySet()) {
            String headerFormat = String.format("%s: %s ", headerEntry.getKey(), headerEntry.getValue());
            stringBuilder.append(headerFormat).append(CRLF);
        }
    }

    private void convertBody(StringBuilder stringBuilder, ResponseBody body) {
        stringBuilder.append(CRLF).append(body.getContent());
    }
}
