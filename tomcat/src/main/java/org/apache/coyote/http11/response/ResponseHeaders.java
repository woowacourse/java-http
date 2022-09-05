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

    public ResponseHeaders postProcess(PostProcessMeta meta) {
        return new ResponseHeaders(headers.stream()
                .map(header -> header.postProcess(meta))
                .collect(Collectors.toList()));
    }

    public ResponseHeaders update(String body) {
        ContentLength contentLength = ContentLength.fromBody(body);
        return replace(contentLength);
    }

    public ResponseHeaders replace(ResponseHeader header) {
        List<ResponseHeader> newHeaders = new ArrayList<>(this.headers);
        newHeaders.removeIf(it -> it.getClass().equals(header.getClass()));
        newHeaders.add(header);
        return new ResponseHeaders(newHeaders);
    }

    @Override
    public String getAsString() {
        return headers.stream()
                .map(ResponseHeader::getAsString)
                .filter(header -> !header.isBlank())
                .collect(Collectors.joining("\n"));
    }
}
