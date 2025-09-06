package org.apache.coyote.http11.http.response;

import http.ContentTypeValue;
import http.HttpHeaderKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.http.common.header.HttpHeader;
import org.apache.coyote.http11.http.common.startline.HttpVersion;

public class HttpResponse {

    private final HttpResponseLine responseLine;
    private final HttpHeader header;
    private final HttpResponseBody responseBody;

    private HttpResponse(final HttpResponseLine responseLine,
                         final HttpHeader header,
                         final HttpResponseBody responseBody) {
        this.responseLine = responseLine;
        this.header = header;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok() {
        final HttpResponseLine httpResponseLine = HttpResponseLine.of(HttpVersion.HTTP_1_1, HttpStatus.OK);
        final HttpResponseBody httpResponseBody = HttpResponseBody.emptyBody();
        final HttpHeader httpHeader = createHeader(httpResponseBody, null);
        return new HttpResponse(httpResponseLine, httpHeader, httpResponseBody);
    }

    public static HttpResponse ok(final String returnValue) {
        final HttpResponseLine httpResponseLine = HttpResponseLine.of(HttpVersion.HTTP_1_1, HttpStatus.OK);
        final HttpResponseBody httpResponseBody = HttpResponseBody.withString(returnValue);
        final HttpHeader httpHeader = createHeader(httpResponseBody, returnValue);
        return new HttpResponse(httpResponseLine, httpHeader, httpResponseBody);
    }

    private static HttpHeader createHeader(final HttpResponseBody responseBody, final String returnValue) {

        final Map<String, String> responseHeaderInfo = new HashMap<>();
        if (returnValue == null) {
            return HttpHeader.from(responseHeaderInfo);
        }

        responseHeaderInfo.put(HttpHeaderKey.CONTENT_TYPE.getValue(),
                ContentTypeValue.findFormatByPattern(returnValue) + ";charset=utf-8");

        final Optional<String> valueOptional = responseBody.getValue();

        if (valueOptional.isPresent()) {
            responseHeaderInfo.put(HttpHeaderKey.CONTENT_LENGTH.getValue(),
                    Integer.toString(responseBody.getByteLength()));
        }

        return HttpHeader.from(responseHeaderInfo);
    }

    public String getResponseFormat() {
        final List<String> responseLines = getResponseLines();
        return String.join("\r\n", responseLines.toArray(String[]::new));
    }

    private List<String> getResponseLines() {
        final List<String> formatLine = new ArrayList<>();
        formatLine.add(responseLine.getFormat());
        formatLine.addAll(header.getFormat());

        final Optional<String> responseBodyValue = responseBody.getValue();

        if (responseBodyValue.isEmpty()) {
            return formatLine;
        }
        formatLine.add("");
        formatLine.add(responseBodyValue.get());
        return formatLine;
    }
}
