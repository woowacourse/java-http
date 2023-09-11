package org.apache.coyote.http11.message.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.StaticFile;
import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.RequestURI;

public class Response {

    public static final Response DEFAULT = create();

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_DELIMITER = "=";
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String RESPONSE_LINE_DELIMITER = " ";
    private static final String EMPTY = "";
    private static final String CRLF = "\r\n";

    private String httpVersion;
    private HttpStatus httpStatus;
    private ResponseHeaders headers;
    private ResponseBody responseBody;

    public Response(
            final String httpVersion,
            final HttpStatus httpStatus,
            final ResponseHeaders headers,
            final ResponseBody responseBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    private static Response create() {
        final HttpStatus httpStatus = HttpStatus.OK;
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        final ResponseBody responseBody = ResponseBody.DEFAULT;
        return new Response(DEFAULT_HTTP_VERSION, httpStatus, responseHeaders, responseBody);
    }

    public static Response createByTemplate(
            final HttpStatus httpStatus,
            final String templatePath,
            final Map<String, String> headers
    ) {
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        final ContentType contentType = ContentType.findByFileName(templatePath);
        final ResponseBody responseBody = getTemplateBody(templatePath);
        responseHeaders.add(CONTENT_TYPE, contentType.getHeaderValue() + CHARSET_UTF_8);
        responseHeaders.add(CONTENT_LENGTH, responseBody.getContentLength());
        responseHeaders.addAll(headers);
        return new Response(DEFAULT_HTTP_VERSION, httpStatus, responseHeaders, responseBody);
    }

    private static ResponseBody getTemplateBody(final String templatePath) {
        final StaticFile staticFile = new StaticFile(templatePath);
        return new ResponseBody(staticFile.getBody(), ContentType.findByExtension(staticFile.getExtension()));
    }

    public static Response createByTemplate(final HttpStatus httpStatus, final String templatePath) {
        return createByTemplate(httpStatus, templatePath, new HashMap<>());
    }

    public static Response createByTemplate(final RequestURI requestURI) {
        final String templatePath = requestURI.getPath();
        final ResponseBody responseBody = getTemplateBody(templatePath);
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        final ContentType contentType = ContentType.findByFileName(requestURI.getPath());
        responseHeaders.add(CONTENT_TYPE, contentType.getHeaderValue() + CHARSET_UTF_8);
        responseHeaders.add(CONTENT_LENGTH, responseBody.getContentLength());
        return new Response(DEFAULT_HTTP_VERSION, HttpStatus.OK, responseHeaders, responseBody);
    }

    public static Response createByResponseBody(final HttpStatus httpStatus, final ResponseBody responseBody) {
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.add(CONTENT_TYPE, ContentType.HTML.getHeaderValue() + CHARSET_UTF_8);
        responseHeaders.add(CONTENT_LENGTH, responseBody.getContentLength());
        return new Response(DEFAULT_HTTP_VERSION, httpStatus, responseHeaders, responseBody);
    }

    public void setLocation(final String url) {
        this.headers.add(LOCATION, url);
    }

    public void setStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addHeader(final String key, final String value) {
        this.headers.add(key, value);
    }

    public void addCookie(final String key, final String value) {
        final String cookiePair = key + COOKIE_DELIMITER + value;
        addHeader(SET_COOKIE, cookiePair);
    }

    public void setBy(final Response response) {
        this.httpVersion = response.httpVersion;
        this.httpStatus = response.httpStatus;
        this.headers = response.headers;
        this.responseBody = response.responseBody;
    }

    public String getResponse() {
        final List<String> responseData = new ArrayList<>();
        final String responseLine = httpVersion
                + RESPONSE_LINE_DELIMITER
                + httpStatus.getCode()
                + RESPONSE_LINE_DELIMITER
                + httpStatus.getMessage()
                + RESPONSE_LINE_DELIMITER;
        responseData.add(responseLine);

        final List<String> responseHeaderLines = headers.getHeaderLines();
        responseData.addAll(responseHeaderLines);
        responseData.add(EMPTY);

        responseData.add(responseBody.getBody());
        return String.join(CRLF, responseData);
    }
}
