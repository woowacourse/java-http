package org.apache.coyote.http11.response;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.headers.ContentLength;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class ResponseHeaders implements Response {

    private final List<ResponseHeader> headers;

    public ResponseHeaders(List<ResponseHeader> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders from(HttpRequest request, ResponseEntity responseEntity) {
        ContentType contentType = ContentType.findWithExtension(request.getPath());
        ContentLength contentLength = ContentLength.from(responseEntity.getBody());
        return new ResponseHeaders(List.of(contentType, contentLength));
    }

    @Override
    public String getAsString() {
        return headers.stream()
                .map(ResponseHeader::getAsString)
                .collect(Collectors.joining("\n"));
    }
}
