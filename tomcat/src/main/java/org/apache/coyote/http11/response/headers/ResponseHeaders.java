package org.apache.coyote.http11.response.headers;

import org.apache.coyote.http11.common.header.ContentLength;
import org.apache.coyote.http11.common.header.ContentType;
import org.apache.coyote.http11.common.header.HeaderProperty;
import org.apache.coyote.http11.common.header.HeaderValue;
import org.apache.coyote.http11.common.header.SetCookie;
import org.apache.coyote.http11.response.ResponseEntity;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final Map<HeaderProperty, HeaderValue> headers = new EnumMap<>(HeaderProperty.class);

    public static ResponseHeaders from(final ResponseEntity responseEntity) {
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        if (responseEntity.getHttpCookie() != null) {
            responseHeaders.addHeader(new SetCookie(responseEntity.getHttpCookie()));
        }
        responseHeaders.addHeader(new ContentLength(responseEntity.calculateContentLength()));
        responseHeaders.addHeader(new ContentType(responseEntity.getContentType()));

        return responseHeaders;
    }

    public String convertToString() {
        List<String> headerStrings = headers.keySet()
                                            .stream()
                                            .map(property -> headers.get(property).convertToString())
                                            .collect(Collectors.toList());

        return String.join(System.lineSeparator(), headerStrings);
    }

    public void addHeader(final HeaderValue header) {
        headers.put(header.getHeaderProperty(), header);
    }
}
