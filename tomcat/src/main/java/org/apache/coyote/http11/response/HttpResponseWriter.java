package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Body;
import org.apache.coyote.http11.Header;

import java.util.StringJoiner;

public class HttpResponseWriter {

    private final static String LINE_DELIMITER = " ";

    private final String response;

    private HttpResponseWriter(String response) {
        this.response = response;
    }

    public static HttpResponseWriter create(HttpResponse httpResponse) {
        String responseDelimiter = System.lineSeparator();
        String headerAndBodyDelimiter = "";

        String statusLineAndHeader = String.join(responseDelimiter,
                writeStatusLine(httpResponse),
                writeHeader(httpResponse));
        if (Body.EMPTY.equals(httpResponse.body())) {
            return new HttpResponseWriter(statusLineAndHeader);
        }
        return new HttpResponseWriter(
                String.join(responseDelimiter,
                        statusLineAndHeader,
                        headerAndBodyDelimiter,
                        writeBody(httpResponse)));
    }

    private static String writeStatusLine(HttpResponse httpResponse) {
        String statusLineDelimiter = " ";
        StringJoiner statusLineMaker = new StringJoiner(statusLineDelimiter);

        statusLineMaker
                .add(httpResponse.httpVersion().value())
                .add(String.valueOf(httpResponse.httpStatus().statusCode()))
                .add(httpResponse.httpStatus().statusText());
        return statusLineMaker.toString() + LINE_DELIMITER;
    }

    private static String writeHeader(HttpResponse httpResponse) {
        String headerDelimiter = ": ";
        String headerLineDelimiter = System.lineSeparator();
        StringJoiner headerMaker = new StringJoiner(headerLineDelimiter);
        Header header = httpResponse.header();

        for (String headerName : header.getHeaderNames()) {
            String headerValue = header.findHeaderValueByKey(headerName).orElse("");
            if (headerValue.trim().isBlank()) {
                continue;
            }
            headerMaker.add(String.join(headerDelimiter, headerName, headerValue) + LINE_DELIMITER);
        }
        return headerMaker.toString();
    }

    private static String writeBody(HttpResponse httpResponse) {
        return httpResponse.body().message();
    }

    public String response() {
        return response;
    }
}
