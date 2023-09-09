package org.apache.coyote.http;

import java.io.IOException;

public class HttpResponseBuilder {

    private static final String LINE_FEED = "\r\n";
    private static final String SPACE = " ";

    private HttpResponseBuilder() {
    }

    public static String buildStaticFileOkResponse(HttpRequest httpRequest, HttpResponse httpResponse, String path) throws IOException {
        try {
            httpResponse.updateFileMessageBody(path);
        } catch (NullPointerException e) {
            return buildStaticFileNotFoundResponse(httpRequest, httpResponse);
        }
        String status = joinStatus(HttpStatus.OK.getHttpStatusCode(), HttpStatus.OK.getHttpStatusMessage());
        String protocol = httpRequest.getProtocol().getName();

        String startLine = joinStartLine(status, protocol);
        httpResponse.updateStartLine(startLine);

        httpResponse.addHeader(HttpHeader.CONTENT_TYPE.getName(), ContentType.findType(path));
        httpResponse.addHeader(HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(httpResponse.getMessageBody().getBytes().length));

        return httpResponse.joinResponse();
    }

    public static String buildStaticFileRedirectResponse(HttpRequest httpRequest, HttpResponse httpResponse, String redirectPath) throws IOException {
        String status = joinStatus(HttpStatus.REDIRECT.getHttpStatusCode(), HttpStatus.REDIRECT.getHttpStatusMessage());
        String protocol = httpRequest.getProtocol().getName();
        String startLine = joinStartLine(status, protocol);

        httpResponse.updateStartLine(startLine);
        httpResponse.updateFileMessageBody(redirectPath);

        httpResponse.addHeader(HttpHeader.LOCATION.getName(), redirectPath);
        httpResponse.addHeader(HttpHeader.CONTENT_TYPE.getName(), ContentType.HTML.getType());
        httpResponse.addHeader(HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(httpResponse.getMessageBody().getBytes().length));

        return httpResponse.joinResponse();
    }

    private static String joinStatus(String statusCode, String statusMessage) {
        return statusCode + SPACE + statusMessage;
    }

    private static String joinStartLine(String status, String protocol) {
        return protocol + SPACE + status + SPACE + LINE_FEED;
    }

    public static String buildStaticFileNotFoundResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String status = joinStatus(HttpStatus.NOT_FOUND.getHttpStatusCode(), HttpStatus.NOT_FOUND.getHttpStatusMessage());
        String protocol = httpRequest.getProtocol().getName();
        String startLine = joinStartLine(status, protocol);

        httpResponse.updateStartLine(startLine);
        httpResponse.updateFileMessageBody("/404.html");

        httpResponse.addHeader(HttpHeader.CONTENT_TYPE.getName(), ContentType.HTML.getType());
        httpResponse.addHeader(HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(httpResponse.getMessageBody().getBytes().length));

        return httpResponse.joinResponse();
    }

    public static String buildCustomResponse(HttpRequest httpRequest, HttpResponse httpResponse, String content) {
        String status = joinStatus(HttpStatus.OK.getHttpStatusCode(), HttpStatus.OK.getHttpStatusMessage());
        String protocol = httpRequest.getProtocol().getName();

        String startLine = joinStartLine(status, protocol);
        httpResponse.updateStartLine(startLine);
        httpResponse.updateMessageBody(content);

        httpResponse.addHeader(HttpHeader.CONTENT_TYPE.getName(), ContentType.HTML.getType());
        httpResponse.addHeader(HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(httpResponse.getMessageBody().getBytes().length));

        return httpResponse.joinResponse();
    }
}
