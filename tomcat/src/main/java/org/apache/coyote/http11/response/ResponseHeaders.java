package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.response.headers.ContentLength;
import org.apache.coyote.http11.response.headers.ResponseHeader;

public class ResponseHeaders implements Response {

    private final List<ResponseHeader> headers;

    public ResponseHeaders(List<ResponseHeader> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders from(String body) {
        ResponseHeaders headers = new ResponseHeaders(new ArrayList<>());
        headers.append(ContentLength.from(body));
        return headers;
    }

    public void append(ResponseHeader header) {
        headers.remove(header);
        this.headers.add(header);
    }

    @Override
    public String getAsString() {
        return headers.stream()
                .map(ResponseHeader::getAsString)
                .collect(Collectors.joining("\n"));
    }
}
