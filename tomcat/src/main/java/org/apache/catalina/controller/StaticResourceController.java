package org.apache.catalina.controller;

import static org.apache.coyote.http11.FileExtensionType.HTML;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_LENGTH;
import static org.apache.coyote.http11.message.header.HttpHeaderFieldType.CONTENT_TYPE;

import java.util.List;

import org.apache.coyote.http11.FileExtensionType;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.message.body.HttpBody;
import org.apache.coyote.http11.message.header.HttpHeaderAcceptType;
import org.apache.coyote.http11.message.header.HttpHeaderField;
import org.apache.coyote.http11.message.header.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestLine;
import org.apache.coyote.http11.message.request.HttpRequestUri;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.coyote.http11.message.response.HttpStatusLine;

public class StaticResourceController implements Controller {

    private static final String NOT_FOUND_URI = "/404.html";

    @Override
    public boolean canHandle(final HttpRequest request) {
        return false;
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final HttpRequestLine requestLine = request.getRequestLine();
        final HttpRequestUri requestUri = requestLine.getHttpRequestUri();
        final FileReader fileReader = new FileReader();

        try {
            final String fileContent = fileReader.readFileContent(requestUri.getValue());
            setSuccessResponse(requestLine, response, fileContent);
        } catch (IllegalStateException e) {
            setFailResponse(fileReader, requestLine, response);
        }
    }

    private void setSuccessResponse(
            final HttpRequestLine requestLine,
            final HttpResponse response,
            final String content
    ) {
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(), HttpStatus.OK);
        final FileExtensionType fileExtensionType = requestLine.getFileExtensionType().get();
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(CONTENT_TYPE.getValue(),
                        HttpHeaderAcceptType.getByValue(fileExtensionType.getValue().toUpperCase()).getValue() + ";charset=utf-8"),
                new HttpHeaderField(CONTENT_LENGTH.getValue(), String.valueOf(content.length()))
        ));
        final HttpBody httpBody = new HttpBody(content);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }

    private void setFailResponse(
            final FileReader fileReader,
            final HttpRequestLine requestLine,
            final HttpResponse response
    ) {
        final String content = fileReader.readFileContent(NOT_FOUND_URI);
        final HttpStatusLine httpStatusLine = new HttpStatusLine(requestLine.getHttpVersion(),
                HttpStatus.NOT_FOUND);
        final HttpHeaders responseHeader = new HttpHeaders(List.of(
                new HttpHeaderField(CONTENT_TYPE.getValue(), HTML.getValue()),
                new HttpHeaderField(CONTENT_LENGTH.getValue(), String.valueOf(content.length()))
        ));
        final HttpBody httpBody = new HttpBody(content);
        response.setStatusLine(httpStatusLine);
        response.setHeader(responseHeader);
        response.setHttpBody(httpBody);
    }
}
