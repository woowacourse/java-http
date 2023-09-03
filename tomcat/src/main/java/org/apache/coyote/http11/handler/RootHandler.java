package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.ResponseStatus;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;

import java.util.List;

public class RootHandler implements Handler {

    @Override
    public ResponseEntity handle(HttpRequest request) {
        String responseData = "Hello world!";
        List<String> headers = List.of(
                String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getRequestUri()).getContentType()),
                String.join(" ", "Content-Length:", String.valueOf(responseData.getBytes().length))
        );

        return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.OK, headers, responseData);
    }
}
