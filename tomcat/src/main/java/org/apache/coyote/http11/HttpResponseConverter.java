package org.apache.coyote.http11;

import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeaders;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

public class HttpResponseConverter {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private HttpResponseConverter() {
    }

    public static String convert(HttpResponse httpResponse) {
        StringJoiner responseJoiner = new StringJoiner(System.lineSeparator());
        responseJoiner.add(extractStatusLine(httpResponse.getStatusLine()));
        responseJoiner.add(extractHeaders(httpResponse.getHeaders()));
        responseJoiner.add(System.lineSeparator());
        addBody(responseJoiner, httpResponse.getBody());
        return responseJoiner.toString();
    }

    private static String extractStatusLine(StatusLine statusLine) {
        HttpStatusCode httpStatusCode = statusLine.getHttpStatusCode();
        return HTTP_VERSION + " " + httpStatusCode.getStatusCode() + " " + httpStatusCode.name();
    }

    private static String extractHeaders(HttpResponseHeaders headers) {
        Map<String, String> headerValues = headers.getHeaders();
        return headerValues.keySet()
            .stream()
            .map(headerName -> makeHeader(headerName, headerValues.get(headerName)))
            .collect(Collectors.joining(System.lineSeparator()));
    }

    private static String makeHeader(String headerName, String value) {
        return headerName + ": " + value;
    }

    private static void addBody(StringJoiner responseJoiner, ResponseBody body) {
        Optional<String> responseBody = body.getValue();
        if (responseBody.isPresent()) {
            responseJoiner.add(responseBody.get());
        }
    }
}
