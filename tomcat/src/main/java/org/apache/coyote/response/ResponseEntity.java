package org.apache.coyote.response;

import org.apache.coyote.common.FileType;
import org.apache.coyote.http11.FileUtil;
import org.apache.coyote.request.Request;

import java.util.List;

public class ResponseEntity {


    private final ResponseStartLine responseStartLine;
    private final ResponseHeader responseHeader;
    private final String responseBody;

    private ResponseEntity(final ResponseStartLine responseStartLine, final ResponseHeader responseHeader, final String responseBody) {
        this.responseStartLine = responseStartLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static ResponseEntity fromStatic(final Request request, final ResponseStatus responseStatus) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(request.httpVersion(), responseStatus);
        final String responseBody = FileUtil.getResource(request.getRequestUrl());
        final ResponseContentType contentType = new ResponseContentType(request.getContentType());
        final ResponseContentLength contentLength = new ResponseContentLength(responseBody.getBytes().length);
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    public static ResponseEntity fromString(final Request request, final String responseBody, final ResponseStatus responseStatus) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(request.httpVersion(), responseStatus);
        final ResponseContentType contentType = new ResponseContentType(FileType.TEXT.getContentType());
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, responseBody.length()));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    @Override
    public String toString() {
        return String.join(System.lineSeparator(),
                responseStartLine + " ",
                responseHeader + " ",
                "",
                responseBody);
    }
}
