package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.response.headers.ContentLength;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class ResponseHeaders implements Response {

    private final List<ResponseHeader> headers;

    private ResponseHeaders(List<ResponseHeader> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders empty() {
        return new ResponseHeaders(new ArrayList<>());
    }

    public ResponseHeaders update(String body) {
        ContentLength contentLength = ContentLength.from(body);
        return append(contentLength);
    }

    public ResponseHeaders append(ResponseHeader header) {
        List<ResponseHeader> newHeaders = new ArrayList<>(this.headers);
        newHeaders.remove(header);
        newHeaders.add(header);
        return new ResponseHeaders(newHeaders);
    }

    @Override
    public String getAsString() {
        return headers.stream()
                .map(ResponseHeader::getAsString)
                .collect(Collectors.joining("\n"));
    }
}
