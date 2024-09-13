package org.apache.catalina.controller;

import static org.apache.coyote.http11.FileExtensionType.HTML;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_TYPE;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.FileExtensionType;
import org.apache.coyote.http11.FileFinder;
import org.apache.coyote.http11.message.body.HttpBody;
import org.apache.coyote.http11.message.header.HttpHeader;
import org.apache.coyote.http11.message.header.HttpHeaderAcceptType;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestLine;
import org.apache.coyote.http11.message.request.HttpRequestUri;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.coyote.http11.message.response.HttpStatusLine;

public class StaticResourceController implements Controller {

    private static final String NOT_FOUND_URI = "/404.html";

    @Override
    public boolean canHandleRequest(final HttpRequest request) {
        return false;
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpRequestUri requestUri = requestLine.getHttpRequestUri();
        final FileFinder fileFinder = new FileFinder();

        try {
            final Optional<String> content = fileFinder.readFileContent(requestUri.getValue());
            content.ifPresentOrElse(
                    c -> setSuccessResponse(requestLine, response, c),
                    () -> setFailResponse(fileFinder, requestLine, response)
            );
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }
    }

    private void setSuccessResponse(
            final HttpRequestLine requestLine,
            final HttpResponse response,
            final String content
    ) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.OK);
        final FileExtensionType fileExtensionType = requestLine.getFileExtensionType().get();
        final HttpHeader responseHeader = new HttpHeader(Map.of(
                CONTENT_TYPE.getValue(), HttpHeaderAcceptType.getByValue(fileExtensionType.getValue().toUpperCase()).getValue(),
                CONTENT_LENGTH.getValue(), String.valueOf(content.length())
        ));
        final HttpBody httpBody = new HttpBody(content);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    private void setFailResponse(final FileFinder fileFinder, HttpRequestLine requestLine, final HttpResponse response) {
        try {
            final String content = fileFinder.readFileContent(NOT_FOUND_URI).get();
            final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.NOT_FOUND);
            final HttpHeader responseHeader = new HttpHeader(Map.of(
                    CONTENT_TYPE.getValue(), HTML.getValue(),
                    CONTENT_LENGTH.getValue(), String.valueOf(content.length())
            ));
            final HttpBody httpBody = new HttpBody(content);
            response.setStatusLine(httpStatusLine);
            response.setHeader(responseHeader);
            response.setHttpBody(httpBody);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }
    }
}
