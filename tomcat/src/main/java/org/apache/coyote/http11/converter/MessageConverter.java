package org.apache.coyote.http11.converter;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class MessageConverter {

    private static final String CRLF = "\r\n";
    private static final String HEADER_DELIMITER = ": ";
    private static final String SPACE = " ";
    private static final String STATUS_LINE_DELIMITER = " ";

    private MessageConverter() {
    }

    public static String convertHttpResponseToMessage(HttpResponse httpResponse) {
        String statusLine = convertHttpStatusLineToMessage(httpResponse.getStatusLine());
        String headers = convertHttpHeadersToMessage(httpResponse.getHeaders());
        String body = httpResponse.getBody();
        return String.join(
                CRLF,
                statusLine,
                headers,
                "",
                body
        );
    }

    private static String convertHttpStatusLineToMessage(StatusLine statusLine) {
        String httpVersion = statusLine.getHttpVersion();
        HttpStatus httpStatus = statusLine.getHttpStatus();
        return String.join(
                STATUS_LINE_DELIMITER,
                httpVersion,
                String.valueOf(httpStatus.getCode()),
                httpStatus.getValue(),
                ""
        );
    }

    private static String convertHttpHeadersToMessage(Map<String, String> headers) {
        return headers.entrySet().stream()
                .map(MessageConverter::formatHeaderEntry)
                .collect(Collectors.joining(CRLF));
    }

    private static String formatHeaderEntry(Map.Entry<String, String> entry) {
        return entry.getKey() + HEADER_DELIMITER + entry.getValue() + SPACE;
    }
}
