package org.apache.coyote.http;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class HttpResponseBuilder {

    private static final String LINE_FEED = "\r\n";
    private static final String SPACE = " ";

    public static String buildStaticFileOkResponse(HttpRequest httpRequest, HttpResponse httpResponse, String path) throws IOException {
        String status = joinStatus(HttpStatus.OK.getHttpStatusCode(), HttpStatus.OK.getHttpStatusMessage());
        String protocol = httpRequest.getProtocol().getName();

        String startLine = joinStartLine(status, protocol);
        httpResponse.updateStartLine(startLine);
        httpResponse.updateFileMessageBody(path);

        appendCookie(httpRequest, httpResponse);
        httpResponse.addHeader("Content-Type", ContentType.findType(path));
        httpResponse.addHeader("Content-Length", String.valueOf(httpResponse.getMessageBody().getBytes().length));

        return httpResponse.joinResponse();
    }

    private static String joinContentType(String contentType) {
        return contentType + "; charset=utf-8";
    }

    public static String buildStaticFileRedirectResponse(HttpRequest httpRequest, HttpResponse httpResponse, String redirectPath) throws IOException {
        String status = joinStatus(HttpStatus.REDIRECT.getHttpStatusCode(), HttpStatus.REDIRECT.getHttpStatusMessage());
        String protocol = httpRequest.getProtocol().getName();
        String startLine = joinStartLine(status, protocol);

        httpResponse.updateStartLine(startLine);
        httpResponse.updateFileMessageBody(redirectPath);

        appendCookie(httpRequest, httpResponse);
        httpResponse.addHeader("Location", redirectPath);
        httpResponse.addHeader("Content-Type", joinContentType(ContentType.HTML.getType()));
        httpResponse.addHeader("Content-Length", String.valueOf(httpResponse.getMessageBody().getBytes().length));

        return httpResponse.joinResponse();
    }

    private static String joinStatus(String statusCode, String statusMessage) {
        return statusCode + SPACE + statusMessage;
    }

    private static void appendCookie(HttpRequest httpRequest, HttpResponse httpResponse) {
        Set<Map.Entry<String, String>> entries = httpRequest.findCookies().entrySet();
        for (Map.Entry<String, String> entry : entries) {
            httpResponse.addCookie(entry.getKey(), entry.getValue());
        }
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

        appendCookie(httpRequest, httpResponse);
        httpResponse.addHeader("Content-Type", joinContentType(ContentType.HTML.getType()));
        httpResponse.addHeader("Content-Length", String.valueOf(httpResponse.getMessageBody().getBytes().length));

        return httpResponse.joinResponse();
    }

    public static String buildCustomResponse(HttpRequest httpRequest, HttpResponse httpResponse, String content) {
        String status = joinStatus(HttpStatus.OK.getHttpStatusCode(), HttpStatus.OK.getHttpStatusMessage());
        String protocol = httpRequest.getProtocol().getName();

        String startLine = joinStartLine(status, protocol);
        httpResponse.updateStartLine(startLine);
        httpResponse.updateMessageBody(content);

        appendCookie(httpRequest, httpResponse);
        httpResponse.addHeader("Content-Type", joinContentType(ContentType.HTML.getType()));
        httpResponse.addHeader("Content-Length", String.valueOf(httpResponse.getMessageBody().getBytes().length));

        return httpResponse.joinResponse();
    }
}
