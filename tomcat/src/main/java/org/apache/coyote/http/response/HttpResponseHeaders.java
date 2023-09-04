package org.apache.coyote.http.response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpHeaderConsts;

public class HttpResponseHeaders {

    private final HttpHeaders headers;

    private HttpResponseHeaders(final HttpHeaders headers) {
        this.headers = headers;
    }

    public static HttpResponseHeaders of(
            final ContentType contentType,
            final String body,
            final HeaderDto... headerContents
    ) {
        final List<HeaderDto> headerDtos = new ArrayList<>();

        final HeaderDto contentTypeDto = new HeaderDto(HttpHeaderConsts.CONTENT_TYPE, contentType.getContent());
        final HeaderDto contentLengthDto = new HeaderDto(
                HttpHeaderConsts.CONTENT_LENGTH,
                String.valueOf(body.getBytes(StandardCharsets.UTF_8).length)
        );

        headerDtos.add(contentTypeDto);
        headerDtos.add(contentLengthDto);
        headerDtos.addAll(Arrays.asList(headerContents));

        final HttpHeaders headers = HttpHeaders.from(headerDtos);

        return new HttpResponseHeaders(headers);
    }

    public String convertHeaders() {
        return headers.convertHeaders();
    }
}
