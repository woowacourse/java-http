package org.apache.coyote.response;

import org.apache.coyote.common.FileType;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.http11.FileUtil;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.header.*;

import java.util.List;
import java.util.UUID;

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
        final String responseBody = FileUtil.getResource(request.httpVersion(), request.getRequestUrl());
        final ContentType contentType = new ContentType(request.getContentType());
        final ContentLength contentLength = new ContentLength(responseBody.getBytes().length);
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    public static ResponseEntity fromString(final Request request, final String responseBody, final ResponseStatus responseStatus) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(request.httpVersion(), responseStatus);
        final ContentType contentType = new ContentType(FileType.TEXT.getContentType());
        final ContentLength contentLength = new ContentLength(responseBody.length());
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    public static ResponseEntity fromViewPath(final HttpVersion httpVersion, final String viewPath, final ResponseStatus responseStatus) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(httpVersion, responseStatus);
        final String responseBody = FileUtil.getResourceFromViewPath(httpVersion, viewPath);
        final ContentType contentType = new ContentType(FileType.HTML.getContentType());
        final ContentLength contentLength = new ContentLength(responseBody.getBytes().length);
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    public static ResponseEntity fromViewPathWithRedirect(final Request request, final ResponseStatus responseStatus, final String redirectPath) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(request.httpVersion(), responseStatus);
        final String responseBody = FileUtil.getResourceFromViewPath(request.httpVersion(), request.getPath());
        final Location location = new Location(redirectPath);
        final ContentType contentType = new ContentType(FileType.HTML.getContentType());
        final ContentLength contentLength = new ContentLength(responseBody.getBytes().length);
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength, location));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    public static ResponseEntity fromViewPathWithRedirectSetCookie(final Request request, final ResponseStatus responseStatus, final String redirectPath) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(request.httpVersion(), responseStatus);
        final String responseBody = FileUtil.getResourceFromViewPath(request.httpVersion(), request.getPath());
        final Location location = new Location(redirectPath);
        final ContentType contentType = new ContentType(FileType.HTML.getContentType());
        final ContentLength contentLength = new ContentLength(responseBody.getBytes().length);
        if (request.hasJsessionid()) {
            final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength, location));
            return new ResponseEntity(responseStartLine, responseHeader, responseBody);
        }
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength, location, new SetCookie(UUID.randomUUID().toString())));
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
