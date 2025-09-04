package org.apache.coyote.http11.http.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.http.common.header.ContentTypeValue;
import org.apache.coyote.http11.http.common.header.HttpHeader;
import org.apache.coyote.http11.http.common.header.HttpHeaderKey;
import org.apache.coyote.http11.http.common.startline.HttpVersion;

public class HttpResponse {

    private final HttpResponseLine responseLine;
    private final HttpHeader header;
    private final HttpResponseBody responseBody;

    private HttpResponse(final HttpVersion version, final HttpStatus status, final HttpResponseBody responseBody,
                         final String returnValue) {
        this.responseLine = HttpResponseLine.of(version, status);
        this.responseBody = responseBody;
        this.header = createHeader(responseBody, returnValue);
    }

    public static HttpResponse ok() {
        return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, HttpResponseBody.emptyBody(), "");
    }

    public static HttpResponse ok(final String returnValue) {
        return new HttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK, HttpResponseBody.withString(returnValue),
                returnValue);
    }

    private HttpHeader createHeader(final HttpResponseBody responseBody, final String returnValue) {

        final Map<String, String> responseHeaderInfo = new HashMap<>();

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
