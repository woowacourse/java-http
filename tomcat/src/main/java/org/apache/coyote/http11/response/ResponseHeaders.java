package org.apache.coyote.http11.response;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.response.headers.ContentLength;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class ResponseHeaders implements Response {

    private final List<ResponseHeader> headers;

    public ResponseHeaders(List<ResponseHeader> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders from(ResponseEntity responseEntity) {
        ContentLength contentLength = ContentLength.from(responseEntity.getBody());
        return new ResponseHeaders(List.of(responseEntity.getContentType(), contentLength));
    }

    @Override
    public String getAsString() {
        return headers.stream()
                .map(ResponseHeader::getAsString)
                .collect(Collectors.joining("\n"));
    }
}
